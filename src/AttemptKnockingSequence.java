package src;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Objects;

public class AttemptKnockingSequence {
	
	private InetAddress address;
	private int port;
	private ArrayList<SingleKnock> singleKnocks;
	//private ArrayList<SingleKnock> knockSequence;
	//private ArrayList<Integer> knockConnection;

	public AttemptKnockingSequence(InetAddress address, int port) {
		this.setAddress(address);
		this.setPort(port);
		this.singleKnocks = new ArrayList<>();
		//this.knockSequence = new ArrayList<>();
		//this.knockConnection = new ArrayList<>();
	}
	
	public void addSingleKnock(SingleKnock knock) {
//		knockSequence.add(knock);
//		Collections.sort(knockSequence);
		singleKnocks.add(knock);
		Collections.sort(singleKnocks);
	}
	
//	public void addConnetionKnock(int i) {
//		knockConnection.add(i);
//	}

	public ArrayList<SingleKnock> getSingleKnocks() {
//		return knockSequence;
		return singleKnocks;
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

//	public ArrayList<Integer> getConnectionKnocks() {
//		return knockConnection;
//	}
	

	@Override
	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    if (!(obj instanceof AttemptKnockingSequence))
	        return false;
	    if (obj == this)
	        return true;
//	    return ((this.getAddress().equals(((AttemptKnockingSequence) obj).getAddress())) 
//	    		&& (this.getPort() == ((AttemptKnockingSequence) obj).getPort()));
	    AttemptKnockingSequence aks = (AttemptKnockingSequence) obj;
	    return Objects.equals(address, aks.address) && port == aks.port;
	}
	

	@Override
	public int hashCode() {
	    return Objects.hash(address, port);
	}
	
        
}
