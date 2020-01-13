package src;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
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
    private PublicKey publicKey;

    // open datagram socket on client side with localhost address
    public MyClient(InetAddress address, int portNumber, PublicKey publicKey) {
    	try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
    	this.address = address;
    	this.portNumber = portNumber;
    	this.publicKey = publicKey;
    }
 
    // send packets method
    public ArrayList<Integer> sendEcho(String[] knockingSequence) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
    	
    	// get list of random ports of length 'knockingsequence' to be used as sequence of connection ports
    	ArrayList<Integer> connectionPorts = getRandomConnectionSockets(knockingSequence.length);
 
    	// for each element in knockingSequence send a pack as an attempt knock to server
    	for (int i = 0; i < knockingSequence.length; i++) {
    		
    		// get timestamp
    		long time = System.currentTimeMillis();
    		
    		// encrypt string using RSA
            String encryptedString = Base64.getEncoder().encodeToString(RSAEncrypt.encrypt(publicKey, knockingSequence[i] + "," + time + "," + connectionPorts.get(i)));

            // send packet
            buf = encryptedString.getBytes();
    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, portNumber);
    	    try {
    	    	socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		return connectionPorts;
    }
    
    // method to return number of random port numbers up to 65535. Will act as connection sockets.
    public ArrayList<Integer> getRandomConnectionSockets(int packedSequenceSize) {
    	ArrayList<Integer> sock = new ArrayList<>();
    	for (int i = 0; i < packedSequenceSize; i ++) {
    		sock.add(r.nextInt(65535));
    	}
		return sock;
    }

	public void makeConnection(ArrayList<Integer> connect) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
		// loop through each integer in arraylist that represents a connection to server
		for(int connectionPort : connect) {
			// get current time in seconds
			long start = System.currentTimeMillis()/1000;
			//allow connection for 10 seconds before moving to next connection port
			while(System.currentTimeMillis()/1000 - start <= 4) {
				String encryptedString = Base64.getEncoder().encodeToString(RSAEncrypt.encrypt(publicKey, "Hello server from client port - " + connectionPort));

	            // send packets to the server connection port
	            buf = encryptedString.getBytes();
	    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, connectionPort);
	    	    try {
	    	    	Thread.sleep(500);
	    	    	socket.send(packet); 	
				} catch (IOException | InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
}