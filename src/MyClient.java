package src;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class MyClient {
	
	private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;

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
 
    public void sendEcho(String[] knockingSequence, int portNumber) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
        
    	for (int i = 0; i < knockingSequence.length; i++) {
    		long time = System.currentTimeMillis();
            String encryptedString = Base64.getEncoder().encodeToString(RSAEncrypt.encrypt(knockingSequence[i] + "," + time));
            buf = encryptedString.getBytes();
    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
    	    try {
    	    	socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
//    	byte[] buff = new byte[256];
//    	DatagramPacket recievePacket = new DatagramPacket(buff, buff.length);
//        try {
//			socket.receive(recievePacket);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//        String received = new String(recievePacket.getData(), 0, recievePacket.getLength());
//        System.out.println("client: " + received);
//        return received;
    }
 
    public void close() {
        socket.close();
    }
}