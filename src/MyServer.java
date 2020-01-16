package src;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
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

//import org.apache.commons.net.ntp.NTPUDPClient;
//import org.apache.commons.net.ntp.TimeStamp;
//
//import org.pcap4j.core.NotOpenException;
//import org.pcap4j.core.PacketListener;
//import org.pcap4j.core.PcapDumper;
//import org.pcap4j.core.PcapHandle;
//import org.pcap4j.core.PcapNativeException;
//import org.pcap4j.core.PcapNetworkInterface;
//import org.pcap4j.core.PcapStat;
//import org.pcap4j.core.BpfProgram.BpfCompileMode;
//import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
//import org.pcap4j.packet.Packet;
//import org.pcap4j.util.NifSelector;

//import org.jnetpcap.JCaptureHeader;
//import org.jnetpcap.Pcap;
//import org.jnetpcap.PcapBpfProgram;
//import org.jnetpcap.PcapIf;
//import org.jnetpcap.packet.JPacket;
//import org.jnetpcap.packet.JPacketHandler;
//import org.jnetpcap.packet.Payload;
//import org.jnetpcap.protocol.network.Ip4;
//import org.jnetpcap.protocol.tcpip.Udp;

public class MyServer extends Thread {
        
	private DatagramSocket socket;
	private DatagramSocket connectionSocket;
    private boolean running;
    private byte[] buf = new byte[256];
    private ArrayList<Integer> knockingSequence = new ArrayList<Integer>();
    private ArrayList<Integer> connectionKnocks = new ArrayList<Integer>();
    private final ArrayList<Integer> confirmKnockingSequence =  new ArrayList<Integer>(Arrays.asList(5, 7000, 4000, 6543));
    private HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>> hashKnock = new HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>>(); 
    private ArrayList<SingleKnock> knockList = new ArrayList<>();
    private Logger logger = Logger.getLogger("MyLog");
    private FileHandler fh;
	private String privateKey;
    
    // open datagram socket on server with specisied port number
    public MyServer(int pn, String privkey) {
        try {
			socket = new DatagramSocket(pn);
		} catch (SocketException e) {
			e.printStackTrace();
		}
        this.privateKey = privkey;
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
        
        
	    while (running) {
	    	// thread safe section
	    	synchronized(this) {
	        	
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
					String received = RSAEncrypt.decrypt(privateKey, receive);
					String[] values = received.split(",");
					logger.info("Single Knock Attempt ClientIP - " + aks.getAddress() + ": ClientPort - " 
							+ aks.getPort() + ": Port Knock Entered - " + values[0]);
					
					// deal with packet with timestamp in the future
					if (Long.parseLong(values[1]) > System.currentTimeMillis()) {
						logger.severe("Future Time in Packet Knock: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
						hashKnock.get(aks).clear();
						//running = false;
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
		            			//running = false;
		            			acceptClientConnection(connectionKnocks);            			
		            		} else {
		            			// else, connection refused
		            			logger.warning("Incorrect Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
		    					//running = false;
		            		}
		            		
		            		// clear arraylists
		            		knockingSequence.clear();
		            		hashKnock.get(aks).clear();
		            		connectionKnocks.clear();
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


	private void acceptClientConnection(ArrayList<Integer> connectionKnocks) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		
		// loop through each integer in arraylist that represents a connection to server
		for(int connectionPort : connectionKnocks) {
			try {
				connectionSocket = new DatagramSocket(connectionPort);
			} catch (SocketException e) {
				e.printStackTrace();
			}
			// get current time in seconds
			long start = System.currentTimeMillis()/1000;
			//allow connection for 10 seconds before moving to next connection port
			while(System.currentTimeMillis()/1000 - start <= 4) {
	
				// create new packet
		        DatagramPacket packet = new DatagramPacket(buf, buf.length);
		        try {
		        	// recieve new incoming packet
					connectionSocket.receive(packet);
				} catch (IOException e) {
					e.printStackTrace();
				} 
        
		        // address and port number of incoming packet
		        //InetAddress clientAddress = packet.getAddress();
		        //int clientPort = packet.getPort();
		        
		        // recieve message, decrype and split
            	String receive = new String(packet.getData(), 0, packet.getLength());
				String received = RSAEncrypt.decrypt(privateKey, receive);
				
				logger.info("Server recieved packet with information - " + received);
			}
			connectionSocket.close();
		}
	}
}