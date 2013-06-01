/**
 * 
 */
package org.govhack.rest;

import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.govhack.LogParser;
import org.govhack.Observation;

/**
 * @author ametke
 *
 */
@Path("/")
public class RootResource {
	
	@GET
	@Path("observations/{year}/{month}/{day}")
	@Produces(MediaType.APPLICATION_JSON)
	public Map<String, List<Observation>> getObservations(@PathParam("year") String year, @PathParam("month") String month, 
			@PathParam("day") String day) throws IOException, ParseException {
		
		LogParser lp = new LogParser();
		String file = "/Museum Qld "+day+"-"+month+"-"+year+".log";
		InputStream in = this.getClass().getResourceAsStream(file);
		if(in != null) {
			return lp.getNormalisedObservationsMap(in);
		}
		
		return null;
 
	}
	
	public static void main (String[] args) throws IOException, ParseException {
		RootResource rr = new RootResource();
		Map<String, List<Observation>> res = rr.getObservations("2013", "01", "01");
		for(String key : res.keySet()) {
			for(Observation obs : res.get(key)) {
				System.out.println(obs);
			}
		}
	}
	
}
