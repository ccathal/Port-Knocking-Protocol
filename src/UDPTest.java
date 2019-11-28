package src;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.*;

public class UDPTest {
    MyClient client;
    private final int portNumber = 4445;
 
    @Before
    public void setup(){
        new MyServer(portNumber).start();
        client = new MyClient();
    }
 
    @Test
    public void test() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException {
    	String[] knockingSequence = {"5", "7000", "4000", "6543"};
    	String[] knockingSequence2 = {"1", "6000", "6535", "6555"};
        client.sendEcho(knockingSequence, portNumber);
        //System.out.println("here");
        //client.sendEcho(missMatch, portNumber);
        //assertEquals("success", connection);
        client.sendEcho(knockingSequence2, portNumber);
        //assertFalse(connection2.equals("success"));
    }
 
    @After
    public void tearDown() {
//        client.sendEcho("end");
//        client.close();
    }
}