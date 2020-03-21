package src;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import org.junit.*;

public class UDPTest {
    private static MyClient client;
    private static final String address = "192.168.56.101";
    private static final String serverPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzfpsmXHMJvbJZf2eAeesQXzbABl9cpMFT+vkrdoEsRaK8GaprjOsZC32aM4HZinW/S2jmXNeKBhnfPGsNgT3dPRTMaLH9VAh2GsGVms98PLvVTPBmZFloo5nTacPqQWgm8qkPoZfUOyuEqodsfkf0CQK5I84n0GsorEmNNbDlLeA66OcgPto/2LBMqZAPTwGPanHDEpcEB11IkcsdHHmEBLvuyjSvQnORpJJc33SU/L0XyR/MZndwsgUAHxj8SvJ4kfgIpdBcBZiiHEZVjFOdre9YhQ4dTbQDBWODXUsfXNpbWBECihQbC8adwdtVQA2oLDorHpna10V3DzicQrHIQIDAQAB";
    private static final String clientPrivKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCr2dXGetuwg5Tmr23pUbiGIr9EU7g1y4Vwjgz2RHEDcR071e7kf6Sh7/fUwOPrqxRDnSxrJSn+7kQOaBcSgG3nBoblTiOnulTIsVKzqDGYVrYzWqe6sW/Lgu6e6LcK7eudT/oyIvsg/nscIdrvoB6Lxs7Anbnl0DJK2a28SqJly2zZVlST6xg3UnAYERXJepOnY0fTJt1jUalhpHgRmesHz7n7SGWzRd1Yadb+WjfY86XKs6PZ3uwkSPykTP9YRSMo42JHsGBHXczQb2tSFCqVHJFD2oUjvx5DRosme2dvxkwt9cHEzJqz7aHucLeIxzkra0rteJxtnA2qj1GB33jDAgMBAAECggEABdWadWecZ8y/1GkmUSKOIjexyK8S9PiY6i5YcE/4ikZf2LU1rBpNS5YrEtUNp+WiBX2jkWusllQeiWKtTdqAYLSPBd/2Q+6GjF3A0gqmzGY9P1cXlLYLdpxWX5zFAiD7u9aIRwV7Ay7AnT7lAPetsc8pG87/DbdYzcPAK0FZbB1CdcT1N94g4o3QI/3cybDRe+Z57M6DSAYe+1ogRikAqD7QLD2pnLU+ypVspR84Lq+LbLh+2D+hnZ+hiW52s5khzR24A5OOHUNzq7eAfl+tOhLkyNExpEqB79DnUkBPd8ttT2nd0EY3tIPSPfmj8qSFO7aypccz06jPe7Dj0q4RCQKBgQD7auN9gML1xZKdUEWv4CirDh9qlTudvOxnRAIOYdJtpPqB2GIA+6sCFNfWBsgGjmhYxsm+irgcQCxXkRcmExwd8fVfy37d9jwpZ4f/abhmxPx6rJHeK8jGXfPqFgnXWC2lUByla8cND6egss/1D6Oo4WZ+FQVIff9cDnXgHuQ8PQKBgQCu+7CC/SQi03d8+JP3ZNIPopbW/wxCdvIzkHK1IWxFcLNYOxSptVH37r/FEzM8dml2LMSNb4rtM3jmw9JPT4jQ81T4U4NZfisUmwxyBRpYCLgYAUDGjaIYk+Gg0PmXcZUSsU0PrOThNAh6S6JhGmTQlB9zn0jq59R6yQsd58TY/wKBgDIOPeymzioQg/g+GyHBB1fHIAogXBYznv2QVLh4UWTzC8z+P8TiVlG7xJ0gDIMBp1TFfzUoeS9mt21Xvbwe4eI0Yh4IbeHTPHch3bnEWqpbXckuwnvxS6/y1LUuXhc5vxzCrnFg2+iFzWH5N94alwLwnW9M0Bh2vXieiGluvRIhAoGAFeY1/w7DWQByMdfCXPHnQEGu8xVUaXUNdtqbIIQgUsh6CY7LVTn2GjttELMIdIa1SC7uIm1VS75nYSocxgREMTJi7fk1tRuPNLL66cItu9rLf2WYv8C2CrFnSYMd2ZBDgeViqZWPx4eFkBirJ8/v0hLXpNbIf22oL/29QG7jOe0CgYEAlPogHkE+5Q7OfJwabc56PMkNSm2ruFQ7WqLxie0jUXnIDYxr6TUFnrwpAmrwNmsZ+l8NOSjgHwJhb9taJBNGbtXWieNx0TcjeVRoXssV5BKksT2v4PLbjS3Vl/gBiIX3iszLTQBj7QJLNWT1VPKGMA2Buw57gtBZKyC8lQ4LVyc=";
    
//    @Test
//    public void testCorrectKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {	
//    	ArrayList<Integer> correctKnockingSequence = new ArrayList<>(Arrays.asList(5, 7000, 4000, 6543));
//    	
//    	InetAddress ipAddress = null;
//		try {
//			ipAddress = InetAddress.getByName(address);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//    	client = new MyClient(ipAddress, correctKnockingSequence, pubKey, privKey);
//
//    }
    
//    @Test
//    public void testIncorrectKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {	
//        
//    	InetAddress ipAddress = null;
//		try {
//			ipAddress = InetAddress.getByName(address);
//		} catch (UnknownHostException e) {
//			e.printStackTrace();
//		}
//		
//    	ArrayList<Integer> incorrectKnockingSequence = new ArrayList<>(Arrays.asList(5, 7000, 4010, 6543));
//    	client = new MyClient(ipAddress, incorrectKnockingSequence, serverPubKey, clientPrivKey);
//    	client.sendEcho(incorrectKnockingSequence);
//    	
//    	Thread.sleep(7000);
//    	
//    	ArrayList<Integer> correctKnockingSequence = new ArrayList<>(Arrays.asList(5, 7000, 4000, 6543));
//    	//client = new MyClient(ipAddress, correctKnockingSequence, serverPubKey, clientPrivKey);
//    	client.sendEcho(correctKnockingSequence);
//    }  

    
    /*
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
    */
    
    
    // This block of code is used to test late delivery packets
    // MyClient.java method needs to be slightly modified to test this code
    @Test
    public void testLateDeliveryPacket() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
    	
    	InetAddress ipAddress = null;
		try {
			ipAddress = InetAddress.getByName(address);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    	
    	client = new MyClient(ipAddress, serverPubKey, clientPrivKey);
    	
    	long time = System.currentTimeMillis();
    	
        Object[] knockingSequenceP1 = {"5", time};
    	Object[] knockingSequenceP2 = {"7000", Long.sum(time, 2L)};
    	Object[] knockingSequenceP3 = {"4000", Long.sum(time, 4L)};
    	Object[] knockingSequenceP4 = {"6543", Long.sum(time, 6L)};
  
        client.sendOutOfOrderEcho(knockingSequenceP1);        
        client.sendOutOfOrderEcho(knockingSequenceP2);
        client.sendOutOfOrderEcho(knockingSequenceP4);
        client.sendOutOfOrderEcho(knockingSequenceP3);
    }
    
 
    @AfterClass
    public static void tearDown() {
    	// ToDo
    }
}

