package src;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MyClient {
	
	private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;
    private Random r = new Random();
    private String publicKey;
	private String privateKey;

    // open datagram socket on client side with localhost address
    public MyClient(InetAddress address, ArrayList<Integer> knockSequence, String pubkey, String privkey) {
    	try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
    	this.address = address;
    	this.publicKey = pubkey;
    	this.privateKey = privkey;
    	try {
			sendEcho(knockSequence);
		} catch (InvalidKeyException | BadPaddingException | IllegalBlockSizeException | NoSuchPaddingException
				| NoSuchAlgorithmException | InterruptedException e) {
			e.printStackTrace();
		}
    }

	// send packets method
    public ArrayList<Integer> sendEcho(ArrayList<Integer> knockSequence) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
    	
    	// get list of random ports of length 'knockingsequence' to be used as sequence of connection ports
    	ArrayList<Integer> connectionPorts = getRandomConnectionSockets(knockSequence.size());
 
    	// for each element in knockingSequence send a pack as an attempt knock to server
    	for (int i = 0; i < knockSequence.size(); i++) {
    		
    		// get timestamp from ntp
    		long time = System.currentTimeMillis();
    		
    		// message to be encrypted
    		// incl. ntp timestamp, random connection port, prepend server address (authentication purpose), prepend client address (authentication purpose)		
    		String plainText = null;
			try {
				plainText = time + "," + connectionPorts.get(i) + "," + this.address.getHostAddress() + "," + InetAddress.getLocalHost().getHostAddress();
			} catch (UnknownHostException e2) {
				e2.printStackTrace();
			}
			
    		// generate aes key and encrypt message
    		KeyGenerator generator = KeyGenerator.getInstance("AES");
    		generator.init(128); // The AES key size in number of bits
    		SecretKey secKey = generator.generateKey();
    		
    		// encrypt AES key using RSA
            String encryptedKey = Base64.getEncoder().encodeToString(RSAEncrypt.encryptKey(publicKey, secKey));
            
    		// rsa encrypt the key
    		Cipher aesCipher = Cipher.getInstance("AES");
    		try {
				aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
			} catch (InvalidKeyException e1) {
				e1.printStackTrace();
			}
    		String encryptedString = Base64.getEncoder().encodeToString(aesCipher.doFinal(plainText.getBytes()));
            
            String packetData = encryptedString + " " + encryptedKey;
            
            String signedPacket = null;
            try {
				signedPacket = signFile(packetData, privateKey);
			} catch (InvalidKeyException | NoSuchAlgorithmException | SignatureException e1) {
				e1.printStackTrace();
			}
            
            String sendPacket = signedPacket + ";" + packetData;
            System.out.println(System.currentTimeMillis() - time);
            // send packet
            buf = sendPacket.getBytes();
    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, knockSequence.get(i));
    	    try {
    	    	socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		return connectionPorts;
    }
    
    // test method to send out of order packets (testing from UDPTest class)
    public ArrayList<Integer> sendOutOfOrderEcho(Object[] knockingSequenceP) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
    	
    	// get list of random ports of length 'knockingsequence' to be used as sequence of connection ports
    	ArrayList<Integer> connectionPorts = getRandomConnectionSockets(knockingSequenceP.length);
 
    	// for each element in knockingSequence send a pack as an attempt knock to server
    	for (int i = 0; i < knockingSequenceP.length - 1 ; i++) {
    		
    		// get timestamp from ntp
    		long time = (long) knockingSequenceP[1];
    		
    		// message to be encrypted
    		String plainText = time + "," + connectionPorts.get(i);
    		
    		// generate aes key and encrypt message
    		KeyGenerator generator = KeyGenerator.getInstance("AES");
    		generator.init(128); // The AES key size in number of bits
    		SecretKey secKey = generator.generateKey();
    		
    		// rsa encrypt the key
    		Cipher aesCipher = Cipher.getInstance("AES");
    		aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
    		String encryptedKey = Base64.getEncoder().encodeToString(aesCipher.doFinal(plainText.getBytes()));
    		
    		// encrypt string using RSA
            String encryptedString = Base64.getEncoder().encodeToString(RSAEncrypt.encryptKey(publicKey, secKey));
            
            String packetData = encryptedKey + " " + encryptedString;
            
            // send packet
            this.buf = packetData.getBytes();
    	    DatagramPacket packet = new DatagramPacket(this.buf, this.buf.length, this.address, Integer.parseInt((String) knockingSequenceP[0]));
    	    try {
    	    	socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
		return connectionPorts;
    }
    
    // method to return number of random port numbers up to 65535. Will act as connection sockets.
    private ArrayList<Integer> getRandomConnectionSockets(int packedSequenceSize) {
    	ArrayList<Integer> sock = new ArrayList<>();
    	for (int i = 0; i < packedSequenceSize; i ++) {
    		sock.add(r.nextInt(65535));
    	}
		return sock;
    }
    
    private String signFile(String text, String privKey) throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {  
    	// sign encrypted data using clients private key
    	Signature sign = Signature.getInstance("SHA256withRSA");
        sign.initSign(RSAEncrypt.getPrivateKey(privateKey));
        sign.update(text.getBytes(UTF_8));
        
        byte[] signature = sign.sign();
        return Base64.getEncoder().encodeToString(signature);
    }

    
    // method for making a conection to the server after sending the knock sequence
    // not currently implemented in main code
    // implements port switching
	public void makeConnection(ArrayList<Integer> correctConnectArr) throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
		// loop through each integer in arraylist that represents a connection to server
		for(int correctConnect : correctConnectArr) {
			Thread.sleep(1000);
			// get current time in seconds
			long start = System.currentTimeMillis()/1000;
			//allow connection for 10 seconds before moving to next connection port
			while(System.currentTimeMillis()/1000 - start <= 10) {
				
				String plainText = "Hello server from client  to your port - " + correctConnect;
				
				// generate aes key and encrypt message
	    		KeyGenerator generator = KeyGenerator.getInstance("AES");
	    		generator.init(128); // The AES key size in number of bits
	    		SecretKey secKey = generator.generateKey();
	    		
	    		// rsa encrypt the key
	    		Cipher aesCipher = Cipher.getInstance("AES");
	    		aesCipher.init(Cipher.ENCRYPT_MODE, secKey);
	    		String encryptedKey = Base64.getEncoder().encodeToString(aesCipher.doFinal(plainText.getBytes()));
	    		
	    		// encrypt string using RSA
	            String encryptedString = Base64.getEncoder().encodeToString(RSAEncrypt.encryptKey(publicKey, secKey));
	            
	            String packetData = encryptedKey + " " + encryptedString;

	            // send packets to the server connection port
	            buf = packetData.getBytes();
	    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, correctConnect);
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
