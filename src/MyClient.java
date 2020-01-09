package src;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MyClient {
	
	private DatagramSocket socket;
    private InetAddress address;
    private int portNumber;
    private byte[] buf;
    private Random r = new Random();

    // open datagram socket on client side with localhost address
    public MyClient(InetAddress address, int portNumber) {
    	try {
			socket = new DatagramSocket();
			this.address = address;
        	this.portNumber = portNumber;
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }
 
    // send packets method
    public void sendEcho(String[] knockingSequence) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
    	
    	// get list of random ports of length 'knockingsequence' to be used as sequence of connection ports
    	ArrayList<Integer> connectionPorts = getRandomConnectionSockets(knockingSequence.length);
    	
    	// for each element in knockingSequence send a pack as an attempt knock to server
    	for (int i = 0; i < knockingSequence.length; i++) {
    		
    		// get timestamp
    		long time = System.currentTimeMillis();
    		
    		// encrypt string using RSA
            String encryptedString = Base64.getEncoder().encodeToString(RSAEncrypt.encrypt(knockingSequence[i] + "," + time + "," + connectionPorts.get(i)));

            // send packet
            buf = encryptedString.getBytes();
    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portNumber);
    	    try {
    	    	socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
    
    // method to return number of random port numbers up to 65535. Will act as connection sockets.
    public ArrayList<Integer> getRandomConnectionSockets(int packedSequenceSize) {
    	ArrayList<Integer> sock = new ArrayList<>();
    	for (int i = 0; i < packedSequenceSize; i ++) {
    		sock.add(r.nextInt(65535));
    	}
		return sock;
    }
    
    public void close() {
        socket.close();
    }
}