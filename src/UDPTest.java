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
    private static MyClient client;
    private static final int portNumber = 4445;
    private static final String address = "localhost";
    private static final String pubKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcRjIlTmZP+kQ221YTArb3/xpqBWMMBBVEjGhSh46PnWNfkaPRR2dXJHrk3rKhPCSNEoZPJ7ng/Bn8QBtG8rR9fF0mWfNeUDE0fJqQGZrBsw0/nEsjkgy8O9Q1+5IEgTVc1cOJsyEEeKQ5dSvzLpUXBPRxqjp/j1MV53Qpdt1NgQIDAQAB";
    private static final String privKey = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJxGMiVOZk/6RDbbVhMCtvf/GmoFYwwEFUSMaFKHjo+dY1+Ro9FHZ1ckeuTesqE8JI0Shk8nueD8GfxAG0bytH18XSZZ815QMTR8mpAZmsGzDT+cSyOSDLw71DX7kgSBNVzVw4mzIQR4pDl1K/MulRcE9HGqOn+PUxXndCl23U2BAgMBAAECgYEAhaIZO4GhR/7w2iARqMwHfmZtRgA5RIsxTJ7sjrZQmEq0MYMvHMT8f644UQKGqg3uC5ytsX59GwE5j1Wafb8Jy3AOKDoChSeExGoDPcXTZArM7CAvXi653X4xkrvN02b8D01UZFTZAE/tSupN3Lfcj6r9zp0PWBkKhWA35bcjHOECQQDzoGKKpXA4UlKifCrwu5Lv8oWFoJTRHrGytJqauRlAC2DluAeHrsidIF05Uaiy8lg2eiwH8+KZ47QD17oC/Wy9AkEApDYP9SbG5kyNF/+bmKLpvR2scYRZY8m+KIq47U+UvfSE1++6OPrNey1+aKuem8ni2aKU9TZyPKzBk4TfuGQKFQJAeEiWfoeh+VzDyc9uT/78VBW0UL5w2zLBX08GCiAbVGCJzcFnjlkAWXuSK2ui0/8NCJCXTrHeDka7KS6Ie1NuLQJAELPXB658CKy8pTZAk1PuxmegRKObnATHLMR/btPrYy7d3EDsBiOshtznwKnEJkBwrIZW9GInWHiR7/lR8CVsyQJALACfKVLOTsMt2EM+oVHejcvhxcKHPVmGCTVd+wyh82dkRFY7DUqW4JfMgLj0+yRfG+BGigixFB6AW6k3vRyQrA==";
    
//    @BeforeClass
//    public static void setup() throws NoSuchAlgorithmException, UnknownHostException {
//    	//RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
//    	
//    	// Base64 encoding the public and private keys to ease the sharing of these keys
//    	//String pubKey = Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded());
//    	//String privKey = Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded());
//
//        //PublicKey pubKey = keyPairGenerator.getPublicKey();
//        //PrivateKey privKey = keyPairGenerator.getPrivateKey();
//    	//InetAddress ipAddress = InetAddress.getByName(address);
//    	
//    	new MyServer(portNumber, privKey).start();
//    	//client = new MyClient(ipAddress, portNumber, pubKey);
//    }
    
//    @Test
//    public void testCorrectKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {	
//    	String[] correctKnockingSequence = {"5", "7000", "4000", "6543"};       
//    	ArrayList<Integer> correctConnect = client.sendEcho(correctKnockingSequence);
//    	client.makeConnection(correctConnect);
//    }
//    
//    @Test
//    public void testIncorrectKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {	
//    	String[] incorrectKnockingSequence = {"5", "7000", "4010", "6543"};       
//    	ArrayList<Integer> incorrectConnect = client.sendEcho(incorrectKnockingSequence);
//    	client.makeConnection(incorrectConnect);
//    }
//      
//    @Test
//    public void testFragmentedKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
//    	String[] knockingSequenceP1 = {"5"};
//    	String[] knockingSequenceP2 = {"7000"};
//    	String[] knockingSequenceP3 = {"4000", "6543"};
//    	
//    	ArrayList<Integer> connect1 = client.sendEcho(knockingSequenceP1);
//    	ArrayList<Integer> connect2 = client.sendEcho(knockingSequenceP2);
//    	ArrayList<Integer> connect3 = client.sendEcho(knockingSequenceP3);
//        
//        // want to make a connection attempt to the server using the connection knocks
//    	connect1.addAll(connect2);
//    	connect1.addAll(connect3);
//    	
//    	client.makeConnection(connect1);
//    }
    
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
 
//    @AfterClass
//    public static void tearDown() {
//    	// ToDo
//    }
}
