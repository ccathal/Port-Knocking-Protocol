package src;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MyServer extends Thread {
        
	private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private ArrayList<Integer> knockingSequence = new ArrayList<Integer>();
    private ArrayList<Integer> connectionKnocks = new ArrayList<Integer>();
    private final ArrayList<Integer> confirmKnockingSequence =  new ArrayList<Integer>(Arrays.asList(5, 7000, 4000, 6543));
    private HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>> hashKnock = new HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>>(); 
    private ArrayList<SingleKnock> knockList = new ArrayList<>();
    private Logger logger = Logger.getLogger("MyLog");
    private FileHandler fh;
    
    // open datagram socket on server with specisied port number
    public MyServer(int pn) {
        try {
			socket = new DatagramSocket(pn);
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }
    
 
    public void run() {
    	
    	
    	// set up logging file
        try {
			fh = new FileHandler("MyKnockLogFile.log");
		} catch (SecurityException | IOException e2) {
			e2.printStackTrace();
		}
        
		logger.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);
    	
        running = true;
        logger.info("Desired Knock Sequence - " + confirmKnockingSequence);
        
        // thread safe section
        synchronized(this) {
	        while (running) {
	        	
	        	// create new packet
	            DatagramPacket packet = new DatagramPacket(buf, buf.length);
	            try {
	            	// recieve new incoming packet
	    			socket.receive(packet);
	    		} catch (IOException e) {
	    			e.printStackTrace();
	    		} 
	            
	            // address and port number of incoming packet
	            InetAddress clientAddress = packet.getAddress();
	            int clientPort = packet.getPort();
	        	
	            // set up new clientKnockingSequence with client IP and Port if does not already exist
	        	AttemptKnockingSequence aks = new AttemptKnockingSequence(clientAddress, clientPort);
	            
	        	// add the client knocking details to hashmap
	        	if(hashKnock != null) {
	        		// client details already exist
	        		if(hashKnock.containsKey(aks)) {
		        		logger.info("Multiple knock packets from: IP - " + clientAddress + ": Port - " + clientPort);
		        	} else {
		        		// if client details do not exist yet, add to hashmap
		    		    hashKnock.put(aks, knockList);
		        	}
	        	} else {
	        		// if hashmap is empty
	        		hashKnock.put(aks, knockList);
	        	}
	  
	            try {
	            	// recieve message, decrype and split
	            	String receive = new String(packet.getData(), 0, packet.getLength());
					String received = RSAEncrypt.decrypt(receive);
					String[] values = received.split(",");
					logger.info("Single Knock Attempt ClientIP - " + aks.getAddress() + ": ClientPort - " 
							+ aks.getPort() + ": Port Knock Entered - " + values[0]);
					
					// deal with packet with timestamp in the future
					if (Long.parseLong(values[1]) > System.currentTimeMillis()) {
						logger.severe("Future Time in Packet Knock: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
						hashKnock.get(aks).clear();
						running = false;
						break;
					}
					
					// get hashmap array of single knocks
					ArrayList<SingleKnock> arr = hashKnock.get(aks);
					
					// print 'late packet arrival' message
					// when incoming packet timestamp is greater than previous packet timestamp
					if (!(arr.size() == 0) && Long.parseLong(values[1]) < arr.get(arr.size() - 1).getTime()) {
						logger.warning("Late Packet Arrival: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
					}				
				
					// when hashmap array of single knocks is 4 or less
		            if (arr.size() < 5) {
		            	
		            	// add incoming knock to the attempt
		            	SingleKnock single = new SingleKnock(Integer.parseInt(values[0]), Long.parseLong(values[1]), Integer.parseInt(values[2]));
		            	hashKnock.computeIfAbsent(aks, k -> new ArrayList<>()).add(single);
	
		            	// if array size is now full
		            	if(arr.size() == 4) {
		            		
		            		// sort array based on timestamp
		            		Collections.sort(hashKnock.get(aks));
		            		
		            		// create arraylist of the knocking and connection sequence
		            		for (SingleKnock sk : hashKnock.get(aks)) {
		            			knockingSequence.add(sk.getPortKnock());
		            			connectionKnocks.add(sk.getConnectionKnock());
		            		}
		            		
		            		// if knocking sequence matches, connection allowed
		            		if (knockingSequence.equals(confirmKnockingSequence)) {
		            			logger.info("Correct Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
		            			logger.info("Submitting connection ports for allowed connection: Connection Knocks - " + connectionKnocks);
		            			
		            		} else {
		            			// else, connection refused
		            			logger.warning("Incorrect Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
		    					running = false;
		            		}
		            		
		            		// clear arraylists
		            		knockingSequence.clear();
		            		hashKnock.get(aks).clear();
		            		//connectionKnocks.clear();
		            	}          	
		            }
				} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
						| NoSuchPaddingException | SecurityException e1) {
					e1.printStackTrace();
				}  
	        }
        }
        //socket.close();
    }
}