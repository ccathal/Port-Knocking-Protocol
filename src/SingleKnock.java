package src;

public class SingleKnock {
	
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
}
