/**
 * 
 */
package org.govhack;


/**
 * @author ametke
 *
 */
public class Observation {
	
	protected String id;
	protected String time;
	protected int in;
	protected int out;
	
	public Observation() {
		super();
	}

	public Observation(String id, String time, int in, int out) {
		super();
		this.id = id;
		this.time = time;
		this.in = in;
		this.out = out;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getIn() {
		return in;
	}

	public void setIn(int in) {
		this.in = in;
	}

	public int getOut() {
		return out;
	}

	public void setOut(int out) {
		this.out = out;
	}

	@Override
	public String toString() {
		return "Observation [id=" + id + ", time=" + time + ", in=" + in
				+ ", out=" + out + "]";
	}
	
}
