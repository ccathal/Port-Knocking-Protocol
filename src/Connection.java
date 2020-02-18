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
		synchronized(this) {
			for(AttemptKnockingSequence attempt : hm.keySet()) {
				if(attempt.getAddress().equals(this.aks.getAddress()) && attempt.getPort() != this.aks.getPort() && !this.hm.get(new AttemptKnockingSequence(attempt.getAddress(),attempt.getPort())).isEmpty()) {
					notInitialiseConnection = true;
					break;
				}
			}
		}
		
		if(!notInitialiseConnection) {
			initiateConnection();
		}
		run();
	}
	
	public void portAppend() throws InterruptedException {
		
		this.aks.setSubmittedConnection(true);
		List<Integer> connectKnocks = this.getConnectionKnocks();
    	
    	int port = 0;
    	
    	if(!connectKnocks.contains(this.appendedPort)) {
    		port = connectKnocks.get(0);
    	}
    	else if(connectKnocks.indexOf(this.appendedPort) == 3) {
			logger.info("All connections submitted. Disconnection occuring.");
			
			boolean notDropConnection = false;
			synchronized(this) {
				for(AttemptKnockingSequence attempt : hm.keySet()) {
					if(attempt.getAddress().equals(this.aks.getAddress()) && attempt.getPort() != this.aks.getPort() && !this.hm.get(new AttemptKnockingSequence(attempt.getAddress(),attempt.getPort())).isEmpty()) {
							notDropConnection = true;
							break;
					}
				}
			}		
			
			if(!notDropConnection) {
				dropConnection();
			}
			aks.setSubmittedConnection(false);
			synchronized(this) {
				hm.get(aks).clear();
			}
			this.scheduledThreadPool.shutdown();
			return;
		}
		else {
			port = connectKnocks.get(connectKnocks.indexOf(appendedPort) + 1);
		}
		
		String connectPort = Integer.toString(port);

		String acceptTCPMangle = "sudo iptables -t mangle -A PREROUTING -p tcp -s " + this.address + " --dport " + connectPort + " -j MARK --set-mark 1";
    	String acceptTCPNat = "sudo iptables -t nat -A PREROUTING -p tcp -s " + this.address + " --dport " + connectPort + " -j REDIRECT --to-port " + this.endPort;
    	
    	String acceptUDPMangle = "sudo iptables -t mangle -A PREROUTING -p udp -s " + this.address + " --dport " + connectPort + " -j MARK --set-mark 1";
    	String acceptUDPNat = "sudo iptables -t nat -A PREROUTING -p udp -s " + this.address + " --dport " + connectPort + " -j REDIRECT --to-port " + this.endPort;
    	

    	try {
			Runtime.getRuntime().exec(acceptTCPMangle);
			Runtime.getRuntime().exec(acceptTCPNat);
			Runtime.getRuntime().exec(acceptUDPMangle);
			Runtime.getRuntime().exec(acceptUDPNat);

			logger.info("Server serving Client IP - " + aks.getAddress() +  " on connection port - " + connectPort);

		} catch (IOException  e) {
			e.printStackTrace();
		}
    	  	
    	this.setAppendedPort(port);
    	scheduledThreadPool.scheduleAtFixedRate(this, 10, 10, TimeUnit.SECONDS);		
	}
	
	
	public void initiateConnection() {
		
		String acceptTCPInput = "sudo iptables -A INPUT -p tcp -s " + address + " --dport " + endPort + " -m state --state NEW,ESTABLISHED -m mark --mark 1 -j ACCEPT";
    	String acceptTCPOutput = "sudo iptables -A OUTPUT -p tcp --sport " + endPort + " -m state --state ESTABLISHED -j ACCEPT";
    	
    	String acceptUDPInput = "sudo iptables -A INPUT -p udp -s " + address + " --dport " + endPort + " -m state --state NEW,ESTABLISHED -m mark --mark 1 -j ACCEPT";
    	String acceptUDPOutput = "sudo iptables -A OUTPUT -p udp --sport " + endPort + " -m state --state ESTABLISHED -j ACCEPT";
    	
    	try {
			Runtime.getRuntime().exec(acceptTCPInput);
	    	Runtime.getRuntime().exec(acceptTCPOutput);
	    	Runtime.getRuntime().exec(acceptUDPInput);
	    	Runtime.getRuntime().exec(acceptUDPOutput);
    	} catch (IOException e1) {
			e1.printStackTrace();
		}   	
	}
	

	public void run() {
		
		try {
			
			String dropTCPMangle = "sudo iptables -t mangle -D PREROUTING -p tcp -s " + this.address + " --dport " + this.appendedPort + " -j MARK --set-mark 1";
	    	String dropTCPNat = "sudo iptables -t nat -D PREROUTING -p tcp -s " + this.address + " --dport " + this.appendedPort + " -j REDIRECT --to-port " + this.endPort;
	    	
	    	String dropUDPMangle = "sudo iptables -t mangle -D PREROUTING -p udp -s " + this.address + " --dport " + this.appendedPort + " -j MARK --set-mark 1";
	    	String dropUDPNat = "sudo iptables -t nat -D PREROUTING -p udp -s " + this.address + " --dport " + this.appendedPort + " -j REDIRECT --to-port " + this.endPort;
	    	
	    	try {
		    	Runtime.getRuntime().exec(dropTCPMangle);
				Runtime.getRuntime().exec(dropTCPNat);
				Runtime.getRuntime().exec(dropUDPMangle);
				Runtime.getRuntime().exec(dropUDPNat);
	    	} catch (IOException e) {
	    		e.printStackTrace();
	    	}
	    	portAppend();
		} catch (Exception x) {
			x.printStackTrace();
		}
	}
	
	private void dropConnection() {
		
		String dropTCPInput = "sudo iptables -D INPUT -p tcp -s " + this.address + " --dport " + this.endPort + " -m state --state NEW,ESTABLISHED -m mark --mark 1 -j ACCEPT";
		String dropTCPOutput = "sudo iptables -D OUTPUT -p tcp --sport " + this.endPort +  " -m state --state ESTABLISHED -j ACCEPT";
		
		String dropUDPInput = "sudo iptables -D INPUT -p udp -s " + this.address + " --dport " + this.endPort + " -m state --state NEW,ESTABLISHED -m mark --mark 1 -j ACCEPT";
		String dropUDPOutput = "sudo iptables -D OUTPUT -p udp --sport " + this.endPort +  " -m state --state ESTABLISHED -j ACCEPT";
		
		try {
	    	Runtime.getRuntime().exec(dropTCPInput);
	    	Runtime.getRuntime().exec(dropTCPOutput);
	    	Runtime.getRuntime().exec(dropUDPInput);
	    	Runtime.getRuntime().exec(dropUDPOutput);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		logger.info("Server closing connection with Client IP - " + aks.getAddress());
	}
   
    
    public List<Integer> getConnectionKnocks() {
    	List<Integer> list = new ArrayList<>();
    	synchronized(this) {
    		for (SingleKnock sk : singleKnocks) {
    			list.add(sk.getConnectionKnock());
    		}
    	}
    	return list;
    }
    
    
    public void setAppendedPort(int port) {
    	this.appendedPort = port;
    }

}
