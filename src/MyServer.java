package src;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private final ArrayList<Integer> confirmKnockingSequence =  new ArrayList<Integer>(Arrays.asList(5, 7000, 4000, 6543));
    //private KnockingSequenceList attemptKnockingSequence = new KnockingSequenceList();
    private HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>> hashKnock = new HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>>(); 
    private ArrayList<SingleKnock> knockList = new ArrayList<>();
    private Logger logger = Logger.getLogger("MyLog");
    private FileHandler fh;
    
    public MyServer(int pn) {
        try {
			socket = new DatagramSocket(pn);
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }
    
 
    public void run() {
    	
    	
    	//set up logging
        try {
			fh = new FileHandler("MyKnockLogFile.log");
		} catch (SecurityException | IOException e2) {
			e2.printStackTrace();
		}
        
		logger.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);
    	
        running = true;
        logger.info("Desired Knock Sequence" + confirmKnockingSequence);
        
        
        
        
        
        // add new attempt knocking sequence if not tried from specific ip and port before
//        if(attemptKnockingSequence != null) {
//        	for(AttemptKnockingSequence akSeq : attemptKnockingSequence) {
//            	if(akSeq.getAddress().equals(clientAddress) && (akSeq.getPort() == clientPort)) {
//            		logger.info("Multiple knock packets from: IP - " + clientAddress + ": Port - " + clientPort);
//            	} else {
//            		AttemptKnockingSequence aks = new AttemptKnockingSequence(clientAddress, clientPort);
//        		    attemptKnockingSequence.add(aks);
//            	}
//        	}
//        } else {
//        	AttemptKnockingSequence aks = new AttemptKnockingSequence(clientAddress, clientPort);
//		    attemptKnockingSequence.add(aks);
//        }
       
        
        
        while (running) {
        	
        	//create new packet
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
            	//recieve new incoming packet
    			socket.receive(packet);
    		} catch (IOException e) {
    			e.printStackTrace();
    		} 
            
            //address and port number of incoming packet
            InetAddress clientAddress = packet.getAddress();
            int clientPort = packet.getPort();
        	
        	AttemptKnockingSequence aks = new AttemptKnockingSequence(clientAddress, clientPort);
            
        	if(hashKnock != null) {
        		if(hashKnock.containsKey(aks)) {
	        		logger.info("Multiple knock packets from: IP - " + clientAddress + ": Port - " + clientPort);
	        	} else {
	    		    hashKnock.put(aks, knockList);
	        	}
        	} else {
	    	  hashKnock.put(aks, knockList);
        	}
	      
        	System.out.println(hashKnock.size());
                 
            // get the attempt knocking sequence with ip and port
			//AttemptKnockingSequence aks = attemptKnockingSequence.get(new AttemptKnockingSequence(clientAddress, clientPort));
        	//************************check
//			ArrayList<SingleKnock> aksKnock = aks.getSingleKnocks();
			
			//System.out.println(attemptKnockingSequence.size());
  
            try {
            	// recieve message, decrype and split
            	String receive = new String(packet.getData(), 0, packet.getLength());
				String received = RSAEncrypt.decrypt(receive);
				String[] values = received.split(",");
				logger.info("Single Knock Attempt ClientIP - " + aks.getAddress() + ": ClientPort - " 
						+ aks.getPort() + ": Port Knock Entered - " + values[0]);
				
				// deal with time
//				if (Long.parseLong(values[1]) > System.currentTimeMillis()) {
//					logger.severe("Future Time in Packet Knock: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
//					running = false;
//					break;
//				}
//				
//				if (!(aksKnock.size() == 0) && Long.parseLong(values[1]) < aksKnock.get(aksKnock.size() - 1).getTime()) {
//					logger.warning("Late Packet Arrival: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
//				}
				
				ArrayList<SingleKnock> arr = hashKnock.get(aks);
				
			
	            if (arr.size() < 5) {
	            	//System.out.println("here");
	            	//System.out.println(aks.getSingleKnocks().size());
	            	// add incoming knock to the attempt
	            	hashKnock.computeIfAbsent(aks, k -> new ArrayList<>()).add(new SingleKnock(Integer.parseInt(values[0]), Long.parseLong(values[1]), Integer.parseInt(values[2])));
	            	System.out.println(arr.size());
	            	//aks.addSingleKnock(new SingleKnock(Integer.parseInt(values[0]), Long.parseLong(values[1]), Integer.parseInt(values[2])));
	            	//System.out.println(aks.getSingleKnock().size());
	            	//aks.addConnetionKnock(aks.getConnectionKnock());
	            	//********aks.addConnetionKnock(Integer.parseInt(values[2]));
	            	if(arr.size() == 4) {
	            		System.out.println("here");
	            		for (SingleKnock sk : aks.getSingleKnocks()) {
	            			knockingSequence.add(sk.getPortKnock());
	            		}
	            		if (knockingSequence.equals(confirmKnockingSequence)) {
	            			logger.info("Correct Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
	            			//**********ArrayList<Integer> connectionKnocks = aks.getConnectionKnocks();
	            			//*********System.out.println(connectionKnocks);
	            			logger.info("Submitting connection ports for allowed connection: Connection Knocks - " + "connectionKnocks");
	            			
	            		} else {
	            			logger.warning("Incorrect Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
	    					running = false;
	            		}
	            		knockingSequence.clear();
	            		aks.getSingleKnocks().clear();
	            	}          	
	            }
			} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
					| NoSuchPaddingException | SecurityException e1) {
				e1.printStackTrace();
			}  
        }
        socket.close();
    }
}