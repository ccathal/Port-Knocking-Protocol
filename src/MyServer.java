package src;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
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
    private KnockingSequenceList attemptKnockingSequence = new KnockingSequenceList();
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
    	
        running = true;
        logger.info("Desired Knock Sequence" + confirmKnockingSequence);
        
        
        //set up logging
        try {
			fh = new FileHandler("MyKnockLogFile.log");
		} catch (SecurityException | IOException e2) {
			e2.printStackTrace();
		}
		logger.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();  
        fh.setFormatter(formatter);
        
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
            
            // add new attempt knocking sequence if not tried from specific ip and port before
            attemptKnockingSequence.add(new AttemptKnockingSequence(clientAddress, clientPort));
            
            // get the attempt knocking sequence with ip and port
			AttemptKnockingSequence aks = attemptKnockingSequence.get(new AttemptKnockingSequence(clientAddress, clientPort));
			ArrayList<SingleKnock> aksKnock = aks.getSingleKnock();
  
            try {
            	// recieve message, decrype and split
            	String receive = new String(packet.getData(), 0, packet.getLength());
				String received = RSAEncrypt.decrypt(receive);
				String[] values = received.split(",");
				logger.info("Single Knock Attempt IP - " + aks.getAddress() + ": Port - " + aks.getPort() + ": Port Entered - " + values[0]);
				
				// deal with time
				if (Long.parseLong(values[1]) > System.currentTimeMillis()) {
					logger.severe("Future Time in Packet Knock: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
					running = false;
					break;
				}
				
				if (!(aksKnock.size() == 0) && Long.parseLong(values[1]) < aksKnock.get(aksKnock.size() - 1).getTime()) {
					logger.warning("Late Packet Arrival: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
				}

	            if (aks.getSingleKnock().size() < 5) {
	            	// add incoming knock to the attempt
	            	aks.addSingleKnock(new SingleKnock(Integer.parseInt(values[0]),Long.parseLong(values[1])));
	            	        	            	
	            	if(aks.getSingleKnock().size() == 4) {
	            		for (SingleKnock sk : aks.getSingleKnock()) {
	            			knockingSequence.add(sk.getPortKnock());
	            		}
	            		if (knockingSequence.equals(confirmKnockingSequence)) {
	            			logger.info("Correct Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
	            			
	            		} else {
	            			logger.warning("Incorrect Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
	    					running = false;
	            		}
	            		knockingSequence.clear();
	            		aks.getSingleKnock().clear();
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