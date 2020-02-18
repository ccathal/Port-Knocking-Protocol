package src;

import java.net.InetAddress;
import java.util.Objects;

// class for details of the client IP and Port that is knocking
public class AttemptKnockingSequence {
	
	private InetAddress address;
	private int port;
	private boolean submittedConnection;
	private boolean renewingConnection;

	public AttemptKnockingSequence(InetAddress address, int port) {
		if(address.equals(null) || port == 0) {
			throw new IllegalArgumentException();
		} else {
			this.address = address;
			this.port = port;
			this.submittedConnection = false;
			this.renewingConnection = false;
		}
		
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

	// override equals method to check equality (by ip and port) of class object instances
	@Override
	public boolean equals(Object obj) {
	    if (obj == null) return false;
	    if (!(obj instanceof AttemptKnockingSequence))
	        return false;
	    if (obj == this)
	        return true;
	    AttemptKnockingSequence aks = (AttemptKnockingSequence) obj;
	    return Objects.equals(address, aks.address) && port == aks.port;
	}
	
	// override hashcode to return hash of object class instance if has been previously created
	@Override
	public int hashCode() {
	    return Objects.hash(address, port);
	}
	
	public synchronized boolean getSubmittedConnection() {
		return this.submittedConnection;
	}
	
	public synchronized void setSubmittedConnection(boolean connection) {
		this.submittedConnection = connection;
	}
	
	public boolean getRenewingConnection() {
		return this.renewingConnection;
	}
	
	public void setRenewingConnection(boolean connection) {
		this.renewingConnection = connection;
	}
	
        
}
