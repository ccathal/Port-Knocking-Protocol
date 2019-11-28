package src;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;

public class AttemptKnockingSequence {
	
	private InetAddress address;
	private int port;
	private ArrayList<SingleKnock> knockSequence;

	public AttemptKnockingSequence(InetAddress address, int port) {
		this.setAddress(address);
		this.setPort(port);
		this.knockSequence = new ArrayList<>();
	}
	
	public void addSingleKnock(SingleKnock knock) {
		knockSequence.add(knock);
		Collections.sort(knockSequence);
	}

	public ArrayList<SingleKnock> getSingleKnock() {
		return knockSequence;
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public void setAddress(InetAddress address) {
		this.address = address;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}
	
        
}
