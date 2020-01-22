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
	private static DatagramSocket connectionSocket;
    private static boolean running;
    private static byte[] buf = new byte[256];
    private static ArrayList<Integer> knockingSequence = new ArrayList<Integer>();
    private ArrayList<Integer> connectionKnocks = new ArrayList<Integer>();
    private final static ArrayList<Integer> confirmKnockingSequence =  new ArrayList<Integer>(Arrays.asList(5, 7000, 4000, 6543));
    //private HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>> hashKnock = new HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>>();
    private static HashMap<AttemptKnockingSequence, ArrayList<Integer>> hashKnock = new HashMap<AttemptKnockingSequence, ArrayList<Integer>>();
    //private ArrayList<SingleKnock> knockList = new ArrayList<>();
    private static ArrayList<Integer> knockList = new ArrayList<>();
    private static Logger logger = Logger.getLogger("MyLog");
    private static FileHandler fh;
    private static final String privKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJxGMiVOZk/6RDbbVhMCtvf/GmoFYwwEFUSMaFKHjo+dY1+Ro9FHZ1ckeuTesqE8JI0Shk8nueD8GfxAG0bytH18XSZZ815QMTR8mpAZmsGzDT+cSyOSDLw71DX7kgSBNVzVw4mzIQR4pDl1K/MulRcE9HGqOn+PUxXndCl23U2BAgMBAAECgYEAhaIZO4GhR/7w2iARqMwHfmZtRgA5RIsxTJ7sjrZQmEq0MYMvHMT8f644UQKGqg3uC5ytsX59GwE5j1Wafb8Jy3AOKDoChSeExGoDPcXTZArM7CAvXi653X4xkrvN02b8D01UZFTZAE/tSupN3Lfcj6r9zp0PWBkKhWA35bcjHOECQQDzoGKKpXA4UlKifCrwu5Lv8oWFoJTRHrGytJqauRlAC2DluAeHrsidIF05Uaiy8lg2eiwH8+KZ47QD17oC/Wy9AkEApDYP9SbG5kyNF/+bmKLpvR2scYRZY8m+KIq47U+UvfSE1++6OPrNey1+aKuem8ni2aKU9TZyPKzBk4TfuGQKFQJAeEiWfoeh+VzDyc9uT/78VBW0UL5w2zLBX08GCiAbVGCJzcFnjlkAWXuSK2ui0/8NCJCXTrHeDka7KS6Ie1NuLQJAELPXB658CKy8pTZAk1PuxmegRKObnATHLMR/btPrYy7d3EDsBiOshtznwKnEJkBwrIZW9GInWHiR7/lR8CVsyQJALACfKVLOTsMt2EM+oVHejcvhxcKHPVmGCTVd+wyh82dkRFY7DUqW4JfMgLj0+yRfG+BGigixFB6AW6k3vRyQrA==";
    private static String operatingSystem = System.getProperty("os.name");
    
    // open datagram socket on server with specisied port number
    public static void main(String[] args) throws IOException, InterruptedException {
    	String tcpDumpCmd = "/usr/sbin/tcpdump -l -n udp port '(5 or 7000 or 4000 or 6543)'";
		runTCPDUmp(tcpDumpCmd, true);
    }
    
 
    public static void runTCPDUmp(String crunchifyCmd, boolean waitForResult) throws IOException, InterruptedException {
    	
    	Process setup = Runtime.getRuntime().exec("iptables-save > IPTablesbackup.txt");
		setup.waitFor();
		
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
    	
		String tcpdumpCmdResponse = "";
		ProcessBuilder crunchifyProcessBuilder = null;
 
		if (operatingSystem.toLowerCase().contains("window")) {
			// In case of windows run command using "crunchifyCmd"
			crunchifyProcessBuilder = new ProcessBuilder("cmd", "/c", crunchifyCmd);
		} else {
			// In case of Linux/Ubuntu run command using /bin/bash
			crunchifyProcessBuilder = new ProcessBuilder("/bin/bash", "-c", crunchifyCmd);
		}
 
		crunchifyProcessBuilder.redirectErrorStream(true);
 
		try {
			Process process = crunchifyProcessBuilder.start();
			
			if (waitForResult) {
				BufferedReader inputStreamReader = 
						new BufferedReader(new InputStreamReader(process.getInputStream()));
				Thread.sleep(3000);
				inputStreamReader.readLine();
				inputStreamReader.readLine();
				String line = null;
				while ((line = inputStreamReader.readLine()) != null) {
					
					String[] tcpArr = line.split(" ");
					
					int srcIndex = tcpArr[2].toString().lastIndexOf(".");
					String srcIP = tcpArr[2].toString().substring(0,srcIndex);				
					String srcPort = tcpArr[2].toString().substring(srcIndex+1);
					
					int destIndex = tcpArr[4].toString().lastIndexOf(".");
					String destIP = tcpArr[4].toString().substring(0,destIndex);
					String destPort = tcpArr[4].toString().substring(destIndex+1, tcpArr[4].toString().length()-1);
					
					// set up new clientKnockingSequence with client IP and Port if does not already exist
		        	AttemptKnockingSequence aks = new AttemptKnockingSequence(InetAddress.getByName(srcIP), Integer.parseInt(srcPort));
		            
		        	// add the client knocking details to hashmap
		        	if(hashKnock != null) {
		        		// client details already exist
		        		if(hashKnock.containsKey(aks)) {
			        		logger.info("Multiple knock packets from: IP - " + srcIP + ": Port - " + srcPort);
			        	} else {
			        		// if client details do not exist yet, add to hashmap
			    		    hashKnock.put(aks, knockList);
			        	}
		        	} else {
		        		// if hashmap is empty
		        		hashKnock.put(aks, knockList);
		        	}
		        	
		        	// get hashmap array of single knocks
					ArrayList<Integer> arr = hashKnock.get(aks);			

					// when hashmap array of single knocks is 4 or less
		            if (arr.size() < 5) {
		            	
		            	// add incoming knock to the attempt
		            	//SingleKnock single = new SingleKnock(Integer.parseInt(values[0]), Long.parseLong(values[1]), Integer.parseInt(values[2]));
		            	hashKnock.computeIfAbsent(aks, k -> new ArrayList<>()).add(Integer.parseInt(destPort));
	
		            	// if array size is now full
		            	if(arr.size() == 4) {
		            		
		            		System.out.println(hashKnock.get(aks));
		            		
		            		// create arraylist of the knocking and connection sequence
		            		for (Integer sk : hashKnock.get(aks)) {
		            			knockingSequence.add(sk);
		            			//connectionKnocks.add(sk.getConnectionKnock());
		            		}
		            		
		            		// if knocking sequence matches, connection allowed
		            		if (knockingSequence.equals(confirmKnockingSequence)) {
		            			logger.info("Correct Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
		            			//logger.info("Submitting connection ports for allowed connection: Connection Knocks - " + connectionKnocks);
		            			//acceptClientConnection(connectionKnocks);
		            			makeConnection(aks.getAddress(), aks.getPort(), 22);
		            		} else {
		            			// else, connection refused
		            			logger.warning("Incorrect Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
		            		}
		            		
		            		// clear arraylists
		            		knockingSequence.clear();
		            		hashKnock.get(aks).clear();
		            		//connectionKnocks.clear();
		            	}
		            }
				}
				
				BufferedReader errorStreamReader = 
						new BufferedReader(new InputStreamReader(process.getErrorStream()));
				
				while ((line = errorStreamReader.readLine()) != null) {
					System.out.println(line);
				}
				//crunchifyStream.close();
			}
 
		} catch (Exception e) {
			System.out.println("Error Executing tcpdump command" + e);
		}
    }


	private static void makeConnection(InetAddress srcAddress, int srcPort, int connectPort) throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		
		Process connUDP, connTCP, connICMP;
		Process dropUDP, dropTCP, dropICMP;
		
    	String acceptICMPConnectionCmd = "sudo iptables -A INPUT -p icmp -s " + srcAddress.getHostAddress().toString() + " -j ACCEPT";
    	String acceptTCPConnectionCmd = "sudo iptables -A INPUT -p tcp -s " + srcAddress.getHostAddress().toString() + " --dport " + connectPort + " -j ACCEPT";
    	String acceptUDPConnectionCmd = "sudo iptables -A INPUT -p udp -s " + srcAddress.getHostAddress().toString() + " --dport " + connectPort + " -j ACCEPT";

    	String dropICMPConnectionCmd = "sudo iptables -D INPUT -p icmp -s " + srcAddress.getHostAddress().toString() + " -j ACCEPT";
    	String dropTCPConnectionCmd = "sudo iptables -D INPUT -p tcp -s " + srcAddress.getHostAddress().toString() + " --dport " + connectPort + " -j ACCEPT";
    	String dropUDPConnectionCmd = "sudo iptables -D INPUT -p udp -s " + srcAddress.getHostAddress().toString() + " --dport " + connectPort + " -j ACCEPT";

    	try {
			connUDP = Runtime.getRuntime().exec(acceptUDPConnectionCmd);
			connTCP = Runtime.getRuntime().exec(acceptTCPConnectionCmd);
			connICMP = Runtime.getRuntime().exec(acceptICMPConnectionCmd);
			//con.waitFor();
			
			Thread.sleep(30000);
			
			dropUDP = Runtime.getRuntime().exec(dropUDPConnectionCmd);
			dropTCP = Runtime.getRuntime().exec(dropTCPConnectionCmd);
			dropICMP = Runtime.getRuntime().exec(dropICMPConnectionCmd);
			//drop.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


//	public void run() {  	
//    	
//    	// set up logging file
//        try {
//			fh = new FileHandler("MyKnockLogFile.log");
//		} catch (SecurityException | IOException e2) {
//			e2.printStackTrace();
//		}
//        
//		logger.addHandler(fh);
//		SimpleFormatter formatter = new SimpleFormatter();  
//        fh.setFormatter(formatter);
//    	
//        running = true;
//        logger.info("Desired Knock Sequence - " + confirmKnockingSequence);
//        
//        
//	    while (running) {
//	    	// thread safe section
//	    	synchronized(this) {
//	        	
//	        	// create new packet
//	            DatagramPacket packet = new DatagramPacket(buf, buf.length);
//	            try {
//	            	// recieve new incoming packet
//	    			socket.receive(packet);
//	    		} catch (IOException e) {
//	    			e.printStackTrace();
//	    		}
//	            
//	            // address and port number of incoming packet
//	            InetAddress clientAddress = packet.getAddress();
//	            int clientPort = packet.getPort();
//	        	
//	            // set up new clientKnockingSequence with client IP and Port if does not already exist
//	        	AttemptKnockingSequence aks = new AttemptKnockingSequence(clientAddress, clientPort);
//	            
//	        	// add the client knocking details to hashmap
//	        	if(hashKnock != null) {
//	        		// client details already exist
//	        		if(hashKnock.containsKey(aks)) {
//		        		logger.info("Multiple knock packets from: IP - " + clientAddress + ": Port - " + clientPort);
//		        	} else {
//		        		// if client details do not exist yet, add to hashmap
//		    		    hashKnock.put(aks, knockList);
//		        	}
//	        	} else {
//	        		// if hashmap is empty
//	        		hashKnock.put(aks, knockList);
//	        	}
//	  
//	            try {
//	            	// recieve message, decrype and split
//	            	String receive = new String(packet.getData(), 0, packet.getLength());
//					String received = RSAEncrypt.decrypt(privateKey, receive);
//					String[] values = received.split(",");
//					logger.info("Single Knock Attempt ClientIP - " + aks.getAddress() + ": ClientPort - " 
//							+ aks.getPort() + ": Port Knock Entered - " + values[0]);
//					
//					// deal with packet with timestamp in the future
//					if (Long.parseLong(values[1]) > System.currentTimeMillis()) {
//						logger.severe("Future Time in Packet Knock: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
//						hashKnock.get(aks).clear();
//						//running = false;
//						break;
//					}
//					
//					// get hashmap array of single knocks
//					ArrayList<SingleKnock> arr = hashKnock.get(aks);
//					
//					// print 'late packet arrival' message
//					// when incoming packet timestamp is greater than previous packet timestamp
//					if (!(arr.size() == 0) && Long.parseLong(values[1]) < arr.get(arr.size() - 1).getTime()) {
//						logger.warning("Late Packet Arrival: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
//					}				
//
//					// when hashmap array of single knocks is 4 or less
//		            if (arr.size() < 5) {
//		            	
//		            	// add incoming knock to the attempt
//		            	SingleKnock single = new SingleKnock(Integer.parseInt(values[0]), Long.parseLong(values[1]), Integer.parseInt(values[2]));
//		            	hashKnock.computeIfAbsent(aks, k -> new ArrayList<>()).add(single);
//	
//		            	// if array size is now full
//		            	if(arr.size() == 4) {
//		            		
//		            		// sort array based on timestamp
//		            		Collections.sort(hashKnock.get(aks));
//		            		
//		            		// create arraylist of the knocking and connection sequence
//		            		for (SingleKnock sk : hashKnock.get(aks)) {
//		            			knockingSequence.add(sk.getPortKnock());
//		            			connectionKnocks.add(sk.getConnectionKnock());
//		            		}
//		            		
//		            		// if knocking sequence matches, connection allowed
//		            		if (knockingSequence.equals(confirmKnockingSequence)) {
//		            			logger.info("Correct Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
//		            			logger.info("Submitting connection ports for allowed connection: Connection Knocks - " + connectionKnocks);
//		            			//running = false;
//		            			acceptClientConnection(connectionKnocks);            			
//		            		} else {
//		            			// else, connection refused
//		            			logger.warning("Incorrect Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
//		    					//running = false;
//		            		}
//		            		
//		            		// clear arraylists
//		            		knockingSequence.clear();
//		            		hashKnock.get(aks).clear();
//		            		connectionKnocks.clear();
//		            	}
//		            }
//				} catch (InvalidKeyException | IllegalBlockSizeException | BadPaddingException | NoSuchAlgorithmException
//						| NoSuchPaddingException | SecurityException e1) {
//					e1.printStackTrace();
//				}  
//	        }
//        }
//        //socket.close();
//    }


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
				String received = RSAEncrypt.decrypt(privKey, receive);
				
				logger.info("Server recieved packet with information - " + received);
			}
			connectionSocket.close();
		}
	}
}