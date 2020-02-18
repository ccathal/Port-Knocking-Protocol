package src;

import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class MyServer extends Thread {
    private final ArrayList<Integer> confirmKnockingSequence =  new ArrayList<Integer>(Arrays.asList(5, 7000, 4000, 6543));
    private final int confirmKnockingPort = 23;
    private HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>> hashKnock = new HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>>();
    private Logger logger = Logger.getLogger("MyLog");
    private FileHandler fh;
    private final String privKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDN+myZccwm9sll/Z4B56xBfNsAGX1ykwVP6+St2gSxForwZqmuM6xkLfZozgdmKdb9LaOZc14oGGd88aw2BPd09FMxosf1UCHYawZWaz3w8u9VM8GZkWWijmdNpw+pBaCbyqQ+hl9Q7K4Sqh2x+R/QJArkjzifQayisSY01sOUt4Dro5yA+2j/YsEypkA9PAY9qccMSlwQHXUiRyx0ceYQEu+7KNK9Cc5GkklzfdJT8vRfJH8xmd3CyBQAfGPxK8niR+Ail0FwFmKIcRlWMU52t71iFDh1NtAMFY4NdSx9c2ltYEQKKFBsLxp3B21VADagsOisemdrXRXcPOJxCschAgMBAAECggEATLM3xVvOvaOgE06BjAwM5MXtdvgG8qc0jzI0EVjh7l+KlUJlZOzxAMzsuNIfhzeFSvm3tehz41JTFv+XNPZcfzyLzivjccHJGKGh1oRQqGyOGpgPO3Qc+I82gH/5IONrjxfCWVYIIEZB+8lFDYTLB+Kj+8ApQYRfGKYGqB5g7ftL/YNmaUI1ntnIpssXrq08iHbEVA+jDrCvQj9glaJiF4Fxylvzd6Qep2Kh/7sYvEHmPPEaEOJhXOc6uxwSiMGU2eexjNxrQa3P5dv2rEDHgZArNoye5ZVVQpGqF5GagmY7sW5tHm9VkwRCVZo6s3uk7S/OWZmZAZPuuMkHpnU8kQKBgQDr/bDGp503zUfSfUuSlg09ARw0CsUV8vzBAy4tibW6mB1BZc34OZ6bvzww/+yCr6t9oIJKCSp2u0O+g6waYQtxWkrAO4crC+D12mVHLCHuTY2Y6VGw3UfZa3dkf/rYHiuon2KxRvMSirdmfEEJpO9qZx9AvXIntQWckPTzgE/6kwKBgQDfcUpzBo1+RAdM3IGB9Cua+nkMHaJnUmuKVxZH1ViZzfD3xBymMW69bCyqiFOVIoKaC6NEXmQ3qeWvjUZN9cuG8dlWkjLJlP9SNNMARsRE52qLHsG3nW2HI8wPW0EoZdF8qiJYgza10hdDlxz+CregtAsi0h9B/J2LXxbAU+oj+wKBgFvDzlWxH8VvIZqL9jMN/h/Wqqzh8zlRv08eeXpjrjLcq6OefrUjUrWlazZyjflTbg/vtjorzkNVFkai1O19BwIQ5jhR7YGjoNp5DiDa3GbZ6VGoiIeJxEKbM1X1Hgmj0b5EHBBrUmHHZwGHF5M0e5SYfOKjyBwAnCoBg/6byn3ZAoGAEZpQPi2W+gqL9K8ueLlusf/nh1/SSoeAt15TAAe7uioyQKKvixw72CpsfmbNBuO4HECsdRdml8gHs0PS9RNXHGNzNtG/tIfLcYN91/i7P55nk1wx8LAzT8EvM0qCIJec4FBa8lQr/Dj34jhGbXEUtFFayzx4f+9RzggIt9Akkv8CgYEA5EVrvJx5aUBpCD5ldmVOrAXtSNcgiaiSJJu48N6kgPK2iVWkYsLgkpdez1pVG1yl8t9p6UEjgXnFXSXwJB8kvRmaFZ/ef+xk08OSRBWVhYWivl5LoMwcJVje7RQeSiciVyuDgRT3NYDFjJCG4kLX0fbbsulHdWh71CdP2m2cyuc=";
    
    // open datagram socket on server with specific port number
    public static void main(String[] args) throws IOException, InterruptedException {
    	String tcpDumpCmd = "/usr/sbin/tcpdump -A -l -n udp port '(5 or 7000 or 4000 or 6543)'";
    	MyServer server = new MyServer();
    	server.runTCPDUmp(tcpDumpCmd, true);
    }
 
    private void runTCPDUmp(String crunchifyCmd, boolean waitForResult) throws IOException, InterruptedException {
		
    	// set up logging file
        try {
			fh = new FileHandler("MyKnockLogFile.log");
		} catch (SecurityException | IOException e2) {
			e2.printStackTrace();
		}
        
		logger.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);
    	
        logger.info("Desired Knock Sequence - " + confirmKnockingSequence + " : Corresponding Knock Port - " + confirmKnockingPort);
        
        // run command for linux machine
		ProcessBuilder crunchifyProcessBuilder = new ProcessBuilder("/bin/bash", "-c", crunchifyCmd);
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
					
					long arrivalTime = System.currentTimeMillis()/1000;
					
					String[] tcpArr = line.split(" ");

					int srcIndex = tcpArr[2].toString().lastIndexOf(".");
					String srcIP = tcpArr[2].toString().substring(0,srcIndex);				
					String srcPort = tcpArr[2].toString().substring(srcIndex+1);
					
					int destIndex = tcpArr[4].toString().lastIndexOf(".");
					String destIP = tcpArr[4].toString().substring(0,destIndex);
					String destPort = tcpArr[4].toString().substring(destIndex+1, tcpArr[4].toString().length()-1);
					
					// set up new clientKnockingSequence with client IP and Port if does not already exist
					AttemptKnockingSequence aks = null;
					try {	        		
		        		aks = new AttemptKnockingSequence(InetAddress.getByName(srcIP), Integer.parseInt(srcPort));
		        	} catch(IllegalArgumentException ex) {
		        		break;
		        	}
		        	
					ArrayList<SingleKnock> knockList = new ArrayList<>();
		        	// add the client knocking details to hashmap
	        		if(hashKnock.containsKey(aks)) {

	        			// if its over 30 seconds since previous packet is arrived, assume packet loss and timeout occured
	        			if(!hashKnock.get(aks).isEmpty() && (arrivalTime - hashKnock.get(aks).get(hashKnock.get(aks).size() -1).getArrivalTime() > 30.0)) {
	        				logger.warning("Late knock packets from: IP - " + srcIP + ": Port - " + srcPort +  ". Starting port knocking from specified IP address assuming packet loss & timeout occured");
		            		hashKnock.get(aks).clear();
	        			}
		        		logger.info("Multiple knock packets from: IP - " + srcIP + ": Port - " + srcPort);
		        	} else {
		        		// if client details do not exist yet, add to hashmap
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
		            	
	            	// add incoming knock to the attempt
	            	SingleKnock single = null;
	            	try {
	            		single = new SingleKnock(Integer.parseInt(destPort), Long.parseLong(values[0]), Integer.parseInt(values[1]), arrivalTime);
	            	} catch(IllegalArgumentException ex) {
		        		break;
		        	}
	            	
	            	System.out.println(aks.getSubmittedConnection());
	            	System.out.println(hashKnock.get(aks).size() == 4);
	            	System.out.println(!aks.getRenewingConnection());
	            	System.out.println(hashKnock);
	            	if(aks.getSubmittedConnection() && hashKnock.get(aks).size() == 4 && !aks.getRenewingConnection()) {
	            		System.out.println("here");
	            		hashKnock.get(aks).clear();
	            		aks.setRenewingConnection(true);
	            	}
	            	hashKnock.computeIfAbsent(aks, k -> new ArrayList<>()).add(single);

	            	// if array size is now full
	            	if(arr.size() == 4) {
	            		
	            		Collections.sort(hashKnock.get(aks));
	            		ArrayList<Integer> knockSequence = new ArrayList<Integer>();
	            		ArrayList<Integer> connectionSequence = new ArrayList<Integer>();
	            		// create arraylist of the knocking and connection sequence
	            		for (SingleKnock sk : hashKnock.get(aks)) {
	            			knockSequence.add(sk.getPortKnock());
	            			connectionSequence.add(sk.getConnectionKnock());
	            		}
	            		
	            		// if knocking sequence matches, connection allowed
	            		if (knockSequence.equals(confirmKnockingSequence)) {
	            			logger.info("Correct Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
	            			logger.info("Submitting connection ports for allowed connection: Connection Knocks - " + connectionSequence);
            			
	            			System.out.println(aks.getSubmittedConnection());
	            			if(!aks.getSubmittedConnection()) {
	            				new Connection(aks, hashKnock.get(aks), confirmKnockingPort, logger, hashKnock);
	            			} else {
	            				aks.setRenewingConnection(false);
	            			}
	            			
	            		} else {
	            			// else, connection refused
	            			logger.warning("Incorrect Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
	            			hashKnock.get(aks).clear();	            		
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
	
}