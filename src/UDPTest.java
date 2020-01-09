package src;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.*;

public class UDPTest {
    MyClient client;
    private final int portNumber = 4445;
    private final String address = "localhost";
    
    @Before
    public void setup() {
    	new MyServer(portNumber).start();
    	try {
    	    InetAddress ipAddress = InetAddress.getByName(address);
    	    client = new MyClient(ipAddress, portNumber);
    	} catch(UnknownHostException e){
            e.printStackTrace();
        }
    }
 
//    @Test
//    public void testCorrectKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {	
//    	String[] knockingSequence = {"5", "7000", "4000", "6543"};       
//    	client.sendEcho(knockingSequence);
//    }
    
//    @Test
//    public void testIncorrectKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {	
//    	String[] knockingSequence = {"5", "7000", "4010", "6543"};       
//    	client.sendEcho(knockingSequence);
//    }
//      
    @Test
    public void testFragmentedKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
    	String[] knockingSequenceP1 = {"5"};
    	String[] knockingSequenceP2 = {"7000"};
    	String[] knockingSequenceP3 = {"4000", "6543"};
    	
    	client.sendEcho(knockingSequenceP1);
        client.sendEcho(knockingSequenceP2);
        client.sendEcho(knockingSequenceP3);
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
