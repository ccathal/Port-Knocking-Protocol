package src;

public class SingleKnock implements Comparable<SingleKnock> {
	
	private int portKnock;
	private long time;

	public SingleKnock(int portKnock, long time) {
		this.portKnock = portKnock;
		this.time = time;
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
