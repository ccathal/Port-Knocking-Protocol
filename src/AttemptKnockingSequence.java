package src;

import java.net.InetAddress;
import java.util.Objects;

public class AttemptKnockingSequence {
	
	private InetAddress address;
	private int port;

	public AttemptKnockingSequence(InetAddress address, int port) {
		this.port = port;
		this.address = address;
	}
	
	public InetAddress getAddress() {
		return address;
	}

	public int getPort() {
		return port;
	}

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
	

	@Override
	public int hashCode() {
	    return Objects.hash(address, port);
	}
	
        
}
