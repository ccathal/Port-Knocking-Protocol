package src;

import java.util.Objects;

// class for single knock of the knock sequence from client
// currently 'implements comparable' is redundant
public class SingleKnock implements Comparable<SingleKnock> {

	private int portKnock;
	private long ntpTime;
	private int connectionKnock;
	private long arrivalTime;

	public SingleKnock(int portKnock, long ntpTime, int connectionKnock, long arrivalTime) {
		if (portKnock == 0 || ntpTime == 0 || connectionKnock == 0 || arrivalTime == 0) {
			throw new IllegalArgumentException();
		} else {
			this.portKnock = portKnock;
			this.ntpTime = ntpTime;
			this.connectionKnock = connectionKnock;
			this.arrivalTime = arrivalTime;
		}
	}

	public int getConnectionKnock() {
		return this.connectionKnock;
	}

	public int getPortKnock() {
		return this.portKnock;
	}

	public long getTime() {
		return this.ntpTime;
	}

	public long getArrivalTime() {
		return this.arrivalTime;
	}

	// compareTo method to sort single knocks based on time
	@Override
	public int compareTo(SingleKnock s1) {
		if (Objects.equals(this.ntpTime, s1.ntpTime)) {
			return 0;
		} else if (this.ntpTime > s1.ntpTime) {
			return 1;
		} else {
			return -1;
		}
	}
}
