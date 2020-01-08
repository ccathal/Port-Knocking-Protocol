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
    private byte[] buf;
    private Random r = new Random();

    public MyClient() {
    	try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
        try {
			address = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    }
 
    public void sendEcho(String[] knockingSequence, int portNumber) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
    	ArrayList<Integer> connectionPorts = getRandomConnectionSockets(knockingSequence.length);
    	for (int i = 0; i < knockingSequence.length; i++) {
    		long time = System.currentTimeMillis();
    		System.out.println("xx   " + knockingSequence[i]);
            String encryptedString = Base64.getEncoder().encodeToString(RSAEncrypt.encrypt(knockingSequence[i] + "," + time + "," + connectionPorts.get(i)));
            buf = encryptedString.getBytes();
    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
    	    try {
    	    	socket.send(packet);
    	    	Thread.sleep(10);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    }
 
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