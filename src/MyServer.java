package src;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyServer extends Thread {
        
	private DatagramSocket socket;
	private static DatagramSocket connectionSocket;
    private static boolean running;
    private static byte[] buf = new byte[256];
    private static ArrayList<Integer> knockingSequence = new ArrayList<Integer>();
    private static ArrayList<Integer> connectionKnocks = new ArrayList<Integer>();
    private final static ArrayList<Integer> confirmKnockingSequence =  new ArrayList<Integer>(Arrays.asList(5, 7000, 4000, 6543));
    private static HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>> hashKnock = new HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>>();
    private static ArrayList<SingleKnock> knockList = new ArrayList<>();
    private static Logger logger = Logger.getLogger("MyLog");
    private static FileHandler fh;
    private static final String privKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDN+myZccwm9sll/Z4B56xBfNsAGX1ykwVP6+St2gSxForwZqmuM6xkLfZozgdmKdb9LaOZc14oGGd88aw2BPd09FMxosf1UCHYawZWaz3w8u9VM8GZkWWijmdNpw+pBaCbyqQ+hl9Q7K4Sqh2x+R/QJArkjzifQayisSY01sOUt4Dro5yA+2j/YsEypkA9PAY9qccMSlwQHXUiRyx0ceYQEu+7KNK9Cc5GkklzfdJT8vRfJH8xmd3CyBQAfGPxK8niR+Ail0FwFmKIcRlWMU52t71iFDh1NtAMFY4NdSx9c2ltYEQKKFBsLxp3B21VADagsOisemdrXRXcPOJxCschAgMBAAECggEATLM3xVvOvaOgE06BjAwM5MXtdvgG8qc0jzI0EVjh7l+KlUJlZOzxAMzsuNIfhzeFSvm3tehz41JTFv+XNPZcfzyLzivjccHJGKGh1oRQqGyOGpgPO3Qc+I82gH/5IONrjxfCWVYIIEZB+8lFDYTLB+Kj+8ApQYRfGKYGqB5g7ftL/YNmaUI1ntnIpssXrq08iHbEVA+jDrCvQj9glaJiF4Fxylvzd6Qep2Kh/7sYvEHmPPEaEOJhXOc6uxwSiMGU2eexjNxrQa3P5dv2rEDHgZArNoye5ZVVQpGqF5GagmY7sW5tHm9VkwRCVZo6s3uk7S/OWZmZAZPuuMkHpnU8kQKBgQDr/bDGp503zUfSfUuSlg09ARw0CsUV8vzBAy4tibW6mB1BZc34OZ6bvzww/+yCr6t9oIJKCSp2u0O+g6waYQtxWkrAO4crC+D12mVHLCHuTY2Y6VGw3UfZa3dkf/rYHiuon2KxRvMSirdmfEEJpO9qZx9AvXIntQWckPTzgE/6kwKBgQDfcUpzBo1+RAdM3IGB9Cua+nkMHaJnUmuKVxZH1ViZzfD3xBymMW69bCyqiFOVIoKaC6NEXmQ3qeWvjUZN9cuG8dlWkjLJlP9SNNMARsRE52qLHsG3nW2HI8wPW0EoZdF8qiJYgza10hdDlxz+CregtAsi0h9B/J2LXxbAU+oj+wKBgFvDzlWxH8VvIZqL9jMN/h/Wqqzh8zlRv08eeXpjrjLcq6OefrUjUrWlazZyjflTbg/vtjorzkNVFkai1O19BwIQ5jhR7YGjoNp5DiDa3GbZ6VGoiIeJxEKbM1X1Hgmj0b5EHBBrUmHHZwGHF5M0e5SYfOKjyBwAnCoBg/6byn3ZAoGAEZpQPi2W+gqL9K8ueLlusf/nh1/SSoeAt15TAAe7uioyQKKvixw72CpsfmbNBuO4HECsdRdml8gHs0PS9RNXHGNzNtG/tIfLcYN91/i7P55nk1wx8LAzT8EvM0qCIJec4FBa8lQr/Dj34jhGbXEUtFFayzx4f+9RzggIt9Akkv8CgYEA5EVrvJx5aUBpCD5ldmVOrAXtSNcgiaiSJJu48N6kgPK2iVWkYsLgkpdez1pVG1yl8t9p6UEjgXnFXSXwJB8kvRmaFZ/ef+xk08OSRBWVhYWivl5LoMwcJVje7RQeSiciVyuDgRT3NYDFjJCG4kLX0fbbsulHdWh71CdP2m2cyuc=";
    		private static String operatingSystem = System.getProperty("os.name");
    
    // open datagram socket on server with specisied port number
    public static void main(String[] args) throws IOException, InterruptedException {
    	String tcpDumpCmd = "/usr/sbin/tcpdump -A -l -n udp port '(5 or 7000 or 4000 or 6543)'";
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
		        	
		        	String[] packetData = inputStreamReader.readLine().split(" ");
		        	
		        	String aesKey = new String(packetData[1].toString().getBytes(), 0, packetData[1].toString().getBytes().length);
		        	byte[] decryptKey = RSAEncrypt.decrypt(privKey, aesKey);
					SecretKey origionalKey = new SecretKeySpec(decryptKey, 0, decryptKey.length, "AES");
					
					Cipher aesCipher = Cipher.getInstance("AES");
					aesCipher.init(Cipher.DECRYPT_MODE, origionalKey);
					byte[] bytePlainText = aesCipher.doFinal(Base64.getDecoder().decode(packetData[0].toString().substring(28)));
					String plainText = new String(bytePlainText);
					String[] values = plainText.split(",");
					logger.info("Single Knock Attempt ClientIP - " + aks.getAddress() + ": ClientPort - " 
							+ aks.getPort() + ": Port Knock Entered - " + destPort);
		        	
		        	// get hashmap array of single knocks
					ArrayList<SingleKnock> arr = hashKnock.get(aks);			

					// when hashmap array of single knocks is 4 or less
		            if (arr.size() < 5) {
		            	
		            	// add incoming knock to the attempt
		            	SingleKnock single = new SingleKnock(Integer.parseInt(destPort), Long.parseLong(values[0]), Integer.parseInt(values[1]));
		            	hashKnock.computeIfAbsent(aks, k -> new ArrayList<>()).add(single);
	
		            	// if array size is now full
		            	if(arr.size() == 4) {
		            		
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
		            			//acceptClientConnection(connectionKnocks);
		            			makeConnection(aks.getAddress(), aks.getPort(), 22);
		            		} else {
		            			// else, connection refused
		            			logger.warning("Incorrect Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
		            		}
		            		
		            		// clear arraylists
		            		knockingSequence.clear();
		            		hashKnock.get(aks).clear();
		            		connectionKnocks.clear();
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
		String address = srcAddress.getHostAddress().toString();
		
    	String acceptICMPConnectionCmd = "sudo iptables -I INPUT -p icmp -s " + address + " -j ACCEPT";
    	String acceptTCPConnectionCmd = "sudo iptables -I INPUT -p tcp -s " + address + " --dport " + connectPort + " -j ACCEPT";
    	String acceptUDPConnectionCmd = "sudo iptables -I INPUT -p udp -s " + address + " --dport " + connectPort + " -j ACCEPT";

    	String dropICMPConnectionCmd = "sudo iptables -D INPUT -p icmp -s " + address+ " -j ACCEPT";
    	String dropTCPConnectionCmd = "sudo iptables -D INPUT -p tcp -s " + address + " --dport " + connectPort + " -j ACCEPT";
    	String dropUDPConnectionCmd = "sudo iptables -D INPUT -p udp -s " + address + " --dport " + connectPort + " -j ACCEPT";

    	try {
			connUDP = Runtime.getRuntime().exec(acceptUDPConnectionCmd);
			connTCP = Runtime.getRuntime().exec(acceptTCPConnectionCmd);
			connICMP = Runtime.getRuntime().exec(acceptICMPConnectionCmd);
			
			Thread.sleep(100000);
			
			dropUDP = Runtime.getRuntime().exec(dropUDPConnectionCmd);
			dropTCP = Runtime.getRuntime().exec(dropTCPConnectionCmd);
			dropICMP = Runtime.getRuntime().exec(dropICMPConnectionCmd);
			//drop.waitFor();
		} catch (IOException | InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/*
	// run method replaced by runTCPDump() method above
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
    */
	

	/*
	// method to accept client packets at random server sockets using connectionKnocks after correct knock sequence sent
	private void acceptClientConnection(ArrayList<Integer> connectionKnocks)
		throws InvalidKeyException, IllegalBlockSizeException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
		
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
	*/
}