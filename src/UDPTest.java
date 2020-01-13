package src;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.*;

public class UDPTest {
    MyClient client;
    private final int portNumber = 4445;
    private final String address = "localhost";
    
    @Before
    public void setup() throws NoSuchAlgorithmException, UnknownHostException {
    	RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
    	// Base64 encoding the public and private keys to ease the sharing of these keys
//    	String pubKey = Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded());
//    	String privKey = Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded());
//    	System.out.println(pubKey);
//    	System.out.println(privKey);
        PublicKey pubKey = keyPairGenerator.getPublicKey();
        PrivateKey privKey = keyPairGenerator.getPrivateKey();
    	InetAddress ipAddress = InetAddress.getByName(address);
    	
    	new MyServer(portNumber, privKey).start();
    	client = new MyClient(ipAddress, portNumber, pubKey);
    }
    
    @Test
    public void testCorrectKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {	
    	String[] correctKnockingSequence = {"5", "7000", "4000", "6543"};       
    	ArrayList<Integer> correctConnect = client.sendEcho(correctKnockingSequence);
    	client.makeConnection(correctConnect);
    }
    
    @Test
    public void testIncorrectKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {	
    	String[] incorrectKnockingSequence = {"5", "7000", "4010", "6543"};       
    	ArrayList<Integer> incorrectConnect = client.sendEcho(incorrectKnockingSequence);
    	client.makeConnection(incorrectConnect);
    }
      
    @Test
    public void testFragmentedKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
    	String[] knockingSequenceP1 = {"5"};
    	String[] knockingSequenceP2 = {"7000"};
    	String[] knockingSequenceP3 = {"4000", "6543"};
    	
    	ArrayList<Integer> connect1 = client.sendEcho(knockingSequenceP1);
    	ArrayList<Integer> connect2 = client.sendEcho(knockingSequenceP2);
    	ArrayList<Integer> connect3 = client.sendEcho(knockingSequenceP3);
        
        // want to make a connection attempt to the server using the connection knocks
    	connect1.addAll(connect2);
    	connect1.addAll(connect3);
    	
    	client.makeConnection(connect1);
    }
    
    /* This block of code is used to test late delivery packets
     * MyClient.java method needs to be slightly modified to test this code
    @Test
    public void testLateDeliveryPacket() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
        Object[] knockingSequenceP1 = {"5", 123456781L};
    	Object[] knockingSequenceP2 = {"7000", 123456785L};
    	Object[] knockingSequenceP3 = {"4000", 123456787L};
    	Object[] knockingSequenceP4 = {"6543", 123456789L};
  
        client.sendEcho(knockingSequenceP1, portNumber);        
        client.sendEcho(knockingSequenceP2, portNumber);
        client.sendEcho(knockingSequenceP4, portNumber);
        client.sendEcho(knockingSequenceP3, portNumber);
    }
    */
 
    @After
    public void tearDown() {
        //client.sendEcho("end");
        //client.close();
    }
}
