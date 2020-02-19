package src;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

// class to command a connection attempt
public class Connection implements Runnable {
	
	private final List<SingleKnock> singleKnocks;
	private int endPort;
	private Logger logger;
	private String address;
	private int appendedPort;
	private AttemptKnockingSequence aks;
	private Map<AttemptKnockingSequence, ArrayList<SingleKnock>> hm = new HashMap<>();;	
	private ScheduledExecutorService scheduledThreadPool = null;

	public Connection(AttemptKnockingSequence aks, List<SingleKnock> singleKnocks, int endPort, Logger logger, HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>> hashKnock) {
		this.aks = aks;
		this.singleKnocks = Collections.synchronizedList(singleKnocks);
		this.endPort = endPort;
		this.logger = logger;
		this.appendedPort = -1;
		this.hm = Collections.synchronizedMap(hashKnock);
		this.scheduledThreadPool = Executors.newScheduledThreadPool(1);
		this.address = aks.getAddress().getHostAddress();
		
		boolean notInitialiseConnection = false;
		// synchronize as shared hashmap is accessed
		synchronized(this) {
			for(AttemptKnockingSequence attempt : hm.keySet()) {
				// checks to see if a connection is initialized or current connection exists with a client of the same ip
				if(attempt.getAddress().equals(this.aks.getAddress()) && attempt.getPort() != this.aks.getPort() && 
						!this.hm.get(new AttemptKnockingSequence(attempt.getAddress(),attempt.getPort())).isEmpty()) {
					notInitialiseConnection = true;
					break;
				}
			}
		}
		// if a client with the same ip does not currently have a connection
		// initialize connection with associated client ip address
		if(!notInitialiseConnection) {
			initiateConnection();
		}
		// run connection with successful client
		run();
	}
	
	// initiates client-server connection
	// amending INPUT iptables to only accept connections from client ip with marked packed to desired server service on the endPort
	// amending OUTPUT iptables to only send back packets to client ip from desired server service on the endPort
	public void initiateConnection() {
		
		String acceptTCPInput = "sudo iptables -A INPUT -p tcp -s " + this.address + " --dport " + this.endPort + " -m state --state NEW,ESTABLISHED -m mark --mark 1 -j ACCEPT";
    	String acceptTCPOutput = "sudo iptables -A OUTPUT -p tcp --sport " + this.endPort + " -m state --state ESTABLISHED -j ACCEPT";
    	
    	String acceptUDPInput = "sudo iptables -A INPUT -p udp -s " + this.address + " --dport " + this.endPort + " -m state --state NEW,ESTABLISHED -m mark --mark 1 -j ACCEPT";
    	String acceptUDPOutput = "sudo iptables -A OUTPUT -p udp --sport " + this.endPort + " -m state --state ESTABLISHED -j ACCEPT";
    	
    	try {
    		// execute iptables commands
			Runtime.getRuntime().exec(acceptTCPInput).waitFor();
	    	Runtime.getRuntime().exec(acceptTCPOutput).waitFor();
	    	Runtime.getRuntime().exec(acceptUDPInput).waitFor();
	    	Runtime.getRuntime().exec(acceptUDPOutput).waitFor();
    	} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}   	
	}
	

	// run method for client-server connection
	// drops MANGLE and NAT PREROUTING iptables commands that have been previous amended, then amends new connection knock
	public void run() {
		
		try {
			
			String dropTCPMangle = "sudo iptables -t mangle -D PREROUTING -p tcp -s " + this.address + " --dport " + this.appendedPort + " -j MARK --set-mark 1";
	    	String dropTCPNat = "sudo iptables -t nat -D PREROUTING -p tcp -s " + this.address + " --dport " + this.appendedPort + " -j REDIRECT --to-port " + this.endPort;
	    	
	    	String dropUDPMangle = "sudo iptables -t mangle -D PREROUTING -p udp -s " + this.address + " --dport " + this.appendedPort + " -j MARK --set-mark 1";
	    	String dropUDPNat = "sudo iptables -t nat -D PREROUTING -p udp -s " + this.address + " --dport " + this.appendedPort + " -j REDIRECT --to-port " + this.endPort;
	    	
	    	try {
		    	Runtime.getRuntime().exec(dropTCPMangle).waitFor();
				Runtime.getRuntime().exec(dropTCPNat).waitFor();
				Runtime.getRuntime().exec(dropUDPMangle).waitFor();
				Runtime.getRuntime().exec(dropUDPNat).waitFor();
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    	// append new connection knock by manipulating iptables
	    	portAppend();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	// run method for client-server connection
	// amending MANGLE PREROUTING iptables mark packets from client ip address to server connection port
	// amending NAT PREROUTING iptables to route packets of client ip address from server connection port to server desired service at endPort
	public void portAppend() throws InterruptedException {
		
		List<Integer> connectKnocks = this.getConnectionKnocks();
    	
    	int port = 0;
    	
    	// if renewal connection occurs or first time connection from client ip address and port
    	// append first connection knock
    	if(!connectKnocks.contains(this.appendedPort)) {
    		port = connectKnocks.get(0);
    	}
    	// if last connection knock already appended, disconnect client
    	else if(connectKnocks.indexOf(this.appendedPort) == 3) {
			logger.info("All connections submitted. Disconnection occuring.");
			
			boolean notDropConnection = false;
			// synchronize as hashmap is shared
			synchronized(this) {
				// checks to see if a connection is initialized or current connection exists with a client of the same ip
				for(AttemptKnockingSequence attempt : hm.keySet()) {
					if(attempt.getAddress().equals(this.aks.getAddress()) && attempt.getPort() != this.aks.getPort() && !this.hm.get(new AttemptKnockingSequence(attempt.getAddress(),attempt.getPort())).isEmpty()) {
							notDropConnection = true;
							break;
					}
				}
			}		
			
			// if a client with the same ip does not currently have a connection
			// drop connection with associated client ip address
			if(!notDropConnection) {
				dropConnection();
			}
			
			// set submitted connection to false, reset appended port to -1
			// clear hashmap single knocks of associated attempt knocking sequence
			this.aks.setSubmittedConnection(false);
			this.setAppendedPort(-1);
			synchronized(this) {
				hm.get(aks).clear();
			}
			
			// shutdown thread of current client connection
			this.scheduledThreadPool.shutdown();
			return;
		}
    	// else connection knocks left, new connection port retrieved
		else {
			port = connectKnocks.get(connectKnocks.indexOf(appendedPort) + 1);
		}
		
		String connectPort = Integer.toString(port);

		String acceptTCPMangle = "sudo iptables -t mangle -A PREROUTING -p tcp -s " + this.address + " --dport " + connectPort + " -j MARK --set-mark 1";
    	String acceptTCPNat = "sudo iptables -t nat -A PREROUTING -p tcp -s " + this.address + " --dport " + connectPort + " -j REDIRECT --to-port " + this.endPort;
    	
    	String acceptUDPMangle = "sudo iptables -t mangle -A PREROUTING -p udp -s " + this.address + " --dport " + connectPort + " -j MARK --set-mark 1";
    	String acceptUDPNat = "sudo iptables -t nat -A PREROUTING -p udp -s " + this.address + " --dport " + connectPort + " -j REDIRECT --to-port " + this.endPort;	

    	try {
			Runtime.getRuntime().exec(acceptTCPMangle).waitFor();
			Runtime.getRuntime().exec(acceptTCPNat).waitFor();
			Runtime.getRuntime().exec(acceptUDPMangle).waitFor();
			Runtime.getRuntime().exec(acceptUDPNat).waitFor();

			logger.info("Server serving Client IP - " + aks.getAddress() +  " on connection port - " + connectPort);

		} catch (IOException  e) {
			e.printStackTrace();
		}
    	 
    	// set new appended port
    	this.setAppendedPort(port);
    	// schedule thread to run at fixed rate
    	// thread filters between disabling and appending connection knocks depending on knock availability
    	scheduledThreadPool.scheduleAtFixedRate(this, 10, 10, TimeUnit.SECONDS);		
	}
	
	// drops client-server connection
	// deleted INPUT OUTPUT iptables for connections from client ip
	private void dropConnection() {
		
		String dropTCPInput = "sudo iptables -D INPUT -p tcp -s " + this.address + " --dport " + this.endPort + " -m state --state NEW,ESTABLISHED -m mark --mark 1 -j ACCEPT";
		String dropTCPOutput = "sudo iptables -D OUTPUT -p tcp --sport " + this.endPort +  " -m state --state ESTABLISHED -j ACCEPT";
		
		String dropUDPInput = "sudo iptables -D INPUT -p udp -s " + this.address + " --dport " + this.endPort + " -m state --state NEW,ESTABLISHED -m mark --mark 1 -j ACCEPT";
		String dropUDPOutput = "sudo iptables -D OUTPUT -p udp --sport " + this.endPort +  " -m state --state ESTABLISHED -j ACCEPT";
		
		try {
	    	Runtime.getRuntime().exec(dropTCPInput).waitFor();
	    	Runtime.getRuntime().exec(dropTCPOutput).waitFor();
	    	Runtime.getRuntime().exec(dropUDPInput).waitFor();
	    	Runtime.getRuntime().exec(dropUDPOutput).waitFor();
		} catch (IOException | InterruptedException e1) {
			e1.printStackTrace();
		}
		
		logger.info("Server closing connection with Client IP - " + aks.getAddress());
	}
   
	// get method for current connection knock in hashmap associaed with client attempt knocking sequence
    public List<Integer> getConnectionKnocks() {
    	List<Integer> list = new ArrayList<>();
    	// synchronized as hashmap is shared
    	synchronized(this) {
    		for (SingleKnock sk : singleKnocks) {
    			list.add(sk.getConnectionKnock());
    		}
    	}
    	return list;
    }
    
    // set method for current appended connection port
    public void setAppendedPort(int port) {
    	this.appendedPort = port;
    }

}
