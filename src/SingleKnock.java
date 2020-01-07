package src;

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
	
	@Override
	public int compareTo(SingleKnock s1) {
		if(this.getTime() == s1.getTime()) {
			return 0;
		} else if(this.getTime() > s1.getTime()) {
			return 1;
		} else {
			return -1;
		}
	}
}
