/**
 * 
 */
package org.govhack;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author ametke
 *
 */
public class LogParser {
	
	protected SimpleDateFormat sdf = new SimpleDateFormat("yyHH:mm:ss");
	
	// 15 minutes in ms
	public static final long INTERVAL = 900000;
	
	private Observation getObservation(String[] parts) {
		try {
			String dateAndId = parts[0];
			String time = dateAndId.substring(0, 8);
			String id = dateAndId.substring(17, 58).trim();
			int in = Integer.parseInt(parts[1]);
			int out = Integer.parseInt(parts[2]);
			
			// filter
			if(time.compareTo("19:00:00") >= 0) {
				return null;
			}
			
			return new Observation(id, time, in, out);
		} catch(Exception e) {
			return null;
		}
	}
	
	public List<Observation> normaliseObservations(List<Observation> original, long[] intTimes, long minTime) throws ParseException {
		String id = original.get(0).getId();
		List<Observation> res = new ArrayList<>();
		
		long[] times = new long[original.size()];
		int[] ins = new int[times.length];
		int[] outs = new int[times.length];
		
		int i = 0;
		for(Observation obs : original) {
			times[i] = sdf.parse("71"+obs.getTime()).getTime();
			ins[i] = obs.getIn();
			outs[i] = obs.getOut();
			i++;
		}
		
		// Start with the second one
		for(int j = 0; j < intTimes.length - 1; j++) {
			long intTime = (j == 0) ? minTime : intTimes[j-1] + INTERVAL;
			intTimes[j] = intTime;
			int pos = getPos(times, intTime);
			int nextPos = (pos < times.length ) ? pos : pos - 1;
			int prevPos = (nextPos > 0) ? nextPos - 1 : nextPos;
			
			int maxIn = (ins[nextPos] > ins[prevPos]) ? ins[nextPos] : ins[prevPos];
			int minIn = (ins[nextPos] < ins[prevPos]) ? ins[nextPos] : ins[prevPos];
			float inDiff = maxIn - minIn;
			float tick = (float)(times[nextPos] - times[prevPos]);
			float elapsed = (float)(intTime - times[prevPos]);
			float aTickIn = (float)inDiff / (float)tick;
			float valIn = minIn+(aTickIn * elapsed);
			
			int maxOut = (outs[nextPos] > outs[prevPos]) ? outs[nextPos] : outs[prevPos];
			int minOut = (outs[nextPos] < outs[prevPos]) ? outs[nextPos] : outs[prevPos];
			float outDiff = maxOut - minOut;
			float aTickOut = (float)outDiff / (float)tick;
			float valOut = minOut+(aTickOut * elapsed);
			
			res.add(new Observation(id, sdf.format(new Date(intTime)).substring(2), (int) Math.ceil(valIn), 
					(int) Math.ceil(valOut))); 
		}
		
		return res;
		
	}
	
	private int getPos(long[] times, long interval) {
		int res = Arrays.binarySearch(times, interval);
		if(res >= 0) return res;
		
		return (res * -1) - 1;
	}
	
	public List<Observation> getObservations(InputStream in) throws IOException {
		List<Observation> res = new ArrayList<>();
		BufferedReader br = new BufferedReader(new InputStreamReader(in));
		
        String line = br.readLine();

        while (line != null) {
        	String[] parts = line.split("[\\t]");
        	if(parts.length == 3) {
        		Observation o = getObservation(parts);
        		if(o != null)
        			res.add(o);
        	}
        	
        	line = br.readLine();
        	
        }
        
        br.close();
        
        return res;
	}
	
	public Map<String, List<Observation>> getObservationsMap(InputStream in) throws IOException, ParseException {
		Map<String, List<Observation>> res = new HashMap<>();
		
		// Group observations by camera
		for(Observation ob : getObservations(in)) {
			String id = ob.getId();
			List<Observation> obs = res.get(id);
			if(obs == null) {
				obs = new ArrayList<>();
				res.put(id, obs);
			}
			obs.add(ob);
		}
		
		return res;
	}
	
	public Map<String, List<Observation>> getNormalisedObservationsMap(InputStream in) throws IOException, ParseException {
		Map<String, List<Observation>> res = new HashMap<>();
		
		// Group observations by camera
		for(Observation ob : getObservations(in)) {
			String id = ob.getId();
			List<Observation> obs = res.get(id);
			if(obs == null) {
				obs = new ArrayList<>();
				res.put(id, obs);
			}
			obs.add(ob);
		}
		
		long minStartTime = Long.MAX_VALUE;
		long maxStartTime = 0;
		
		for(String key : res.keySet()) {
			List<Observation> obs = res.get(key);
			long time = sdf.parse("71"+obs.get(0).getTime()).getTime();
			if(time < minStartTime) minStartTime = time;
			
			time = sdf.parse("71"+obs.get(obs.size()-1).getTime()).getTime();
			if(time > maxStartTime) maxStartTime = time;
		}
		
		long totalTime = maxStartTime - minStartTime;
		int numIntervals = (int) (totalTime / INTERVAL);
		long[] intTimes = new long[numIntervals];
		intTimes[0] = minStartTime;
		
		for(String key : res.keySet()) {
			res.put(key, normaliseObservations(res.get(key), intTimes, minStartTime));
		}
		
		return res;
	}
	
	/**
	 * @param args
	 * @throws IOException 
	 * @throws ParseException 
	 */
	public static void main(String[] args) throws IOException, ParseException {
		InputStream in = LogParser.class.getClassLoader().getResourceAsStream("test.log");
		LogParser lp = new LogParser();
		Map<String, List<Observation>> map = lp.getNormalisedObservationsMap(in);
		for(String key : map.keySet()) {
			List<Observation> obs = map.get(key);
			for(int j = 0; j < obs.size(); j++) {
				Observation ob = obs.get(j);
				int i = ob.getIn();
				int o = ob.getOut();
				String time = ob.getTime();
				
				int nexti = (j < obs.size() - 1) ? obs.get(j+1).getIn() : 0;
				int nexto = (j < obs.size() - 1) ? obs.get(j+1).getOut() : 0;
				
				int wl = nexto - o;
				int we = nexti - i;
				int curr = i - o;
				
				System.out.println(ob.getId()+","+time+","+i+","+o+","+curr+","+wl+","+we);
			}
		}
		
	}
	
	public List<Flow> getFlow(InputStream in) throws IOException, ParseException {
		List<Flow> res = new ArrayList<>();
		
		Map<String, List<Observation>> map = getObservationsMap(in);
		
		List<Observation> entryCamera = map.get("Level 2 Main Entry");
		List<Observation> scienceCamera = map.get("Science Centre Entry");
		List<Observation> cafeCamera = map.get("Cafe Entry");
		
		long[] intTimes = new long[38];
		long initialTime = sdf.parse("7109:00:00").getTime();
		intTimes[0] = initialTime;
		for(int i = 1; i < intTimes.length; i++) {
			intTimes[i] = intTimes[i-1] + INTERVAL;
		}
		
		entryCamera = normaliseObservations(entryCamera, intTimes, initialTime);
		scienceCamera = normaliseObservations(scienceCamera, intTimes, initialTime);
		cafeCamera = normaliseObservations(cafeCamera, intTimes, initialTime);
		
		int totalIn = 0;
		int prevIn = 0;
		int prevOut = 0;
		int prevScience = 0;
		int prevCafe = 0;
		for(int i = 0; i < intTimes.length-1; i++) {
			Observation entry = entryCamera.get(i);
			Observation science = scienceCamera.get(i);
			Observation cafe = cafeCamera.get(i);
			
			int deltaIn = entry.getIn() - prevIn;
			prevIn = entry.getIn();
			
			int deltaOut = entry.getOut() - prevOut;
			prevOut = entry.getOut();
			
			int deltaCafe = cafe.getIn() - prevCafe;
			prevCafe = cafe.getIn();
			
			int deltaScience = science.getIn() - prevScience;
			prevScience = science.getIn();
			
			totalIn += deltaIn - deltaOut;
			
			float total = deltaCafe + deltaScience;
			float percentageToCafe = (float) deltaCafe / total;
			
			int toCafe = (int)Math.ceil(((float)totalIn * percentageToCafe));
			int toScience = totalIn - toCafe;
			
			res.add(new Flow(sdf.format(new Date(intTimes[i])), totalIn, deltaIn, toCafe, toScience, deltaOut));
		}
		
		return res;
	}

}
