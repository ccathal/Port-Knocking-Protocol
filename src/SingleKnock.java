package src;

import java.util.Objects;

// class for single knock of the knock sequence from client
// currently 'implements comparable' is redundant
public class SingleKnock implements Comparable<SingleKnock> {
	
	private int portKnock;
	private long time;
	private int connectionKnock;

	public SingleKnock(int portKnock, long time, int connectionKnock) {
		this.portKnock = portKnock;
		this.time = time;
		this.connectionKnock = connectionKnock;
	}
	
	public int getConnectionKnock() {
		return connectionKnock;
	}
	
	public int getPortKnock() {
		return portKnock;
	}
	
	public long getTime() {
		return time;
	}
	
	// compareTo method to sort single knocks based on time
	@Override
	public int compareTo(SingleKnock s1) {
		if(Objects.equals(time, s1.time)) {
			return 0;
		} else if(time > s1.time) {
			return 1;
		} else {
			return -1;
		}
	}
}
