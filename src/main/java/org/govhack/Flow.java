/**
 * 
 */
package org.govhack;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author ametke
 *
 */
@XmlRootElement
public class Flow {
	protected String time;
	protected int total;
	protected int in;
	protected int toCafe;
	protected int toLevel1;
	protected int out;

	public Flow() {}
	
	public Flow(String time, int total, int in, int toCafe, int toLevel1, int out) {
		super();
		this.time = time;
		this.total = total;
		this.in = in;
		this.toCafe = toCafe;
		this.toLevel1 = toLevel1;
		this.out = out;
	}
	
	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getTotal() {
		return total;
	}

	public void setTotal(int total) {
		this.total = total;
	}

	public int getToCafe() {
		return toCafe;
	}

	public void setToCafe(int toCafe) {
		this.toCafe = toCafe;
	}

	public int getToLevel1() {
		return toLevel1;
	}

	public void setToLevel1(int toLevel1) {
		this.toLevel1 = toLevel1;
	}

	public int getOut() {
		return out;
	}

	public void setOut(int out) {
		this.out = out;
	}

	public int getIn() {
		return in;
	}

	public void setIn(int in) {
		this.in = in;
	}

	@Override
	public String toString() {
		return "Flow [time=" + time + ", total=" + total + ", in=" + in
				+ ", toCafe=" + toCafe + ", toLevel1=" + toLevel1 + ", out="
				+ out + "]";
	}
	
}
