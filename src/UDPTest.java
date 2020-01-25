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
    private static final String address = "192.168.56.101";
    private static final String pubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzfpsmXHMJvbJZf2eAeesQXzbABl9cpMFT+vkrdoEsRaK8GaprjOsZC32aM4HZinW/S2jmXNeKBhnfPGsNgT3dPRTMaLH9VAh2GsGVms98PLvVTPBmZFloo5nTacPqQWgm8qkPoZfUOyuEqodsfkf0CQK5I84n0GsorEmNNbDlLeA66OcgPto/2LBMqZAPTwGPanHDEpcEB11IkcsdHHmEBLvuyjSvQnORpJJc33SU/L0XyR/MZndwsgUAHxj8SvJ4kfgIpdBcBZiiHEZVjFOdre9YhQ4dTbQDBWODXUsfXNpbWBECihQbC8adwdtVQA2oLDorHpna10V3DzicQrHIQIDAQAB";
    private static final String privKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDN+myZccwm9sll/Z4B56xBfNsAGX1ykwVP6+St2gSxForwZqmuM6xkLfZozgdmKdb9LaOZc14oGGd88aw2BPd09FMxosf1UCHYawZWaz3w8u9VM8GZkWWijmdNpw+pBaCbyqQ+hl9Q7K4Sqh2x+R/QJArkjzifQayisSY01sOUt4Dro5yA+2j/YsEypkA9PAY9qccMSlwQHXUiRyx0ceYQEu+7KNK9Cc5GkklzfdJT8vRfJH8xmd3CyBQAfGPxK8niR+Ail0FwFmKIcRlWMU52t71iFDh1NtAMFY4NdSx9c2ltYEQKKFBsLxp3B21VADagsOisemdrXRXcPOJxCschAgMBAAECggEATLM3xVvOvaOgE06BjAwM5MXtdvgG8qc0jzI0EVjh7l+KlUJlZOzxAMzsuNIfhzeFSvm3tehz41JTFv+XNPZcfzyLzivjccHJGKGh1oRQqGyOGpgPO3Qc+I82gH/5IONrjxfCWVYIIEZB+8lFDYTLB+Kj+8ApQYRfGKYGqB5g7ftL/YNmaUI1ntnIpssXrq08iHbEVA+jDrCvQj9glaJiF4Fxylvzd6Qep2Kh/7sYvEHmPPEaEOJhXOc6uxwSiMGU2eexjNxrQa3P5dv2rEDHgZArNoye5ZVVQpGqF5GagmY7sW5tHm9VkwRCVZo6s3uk7S/OWZmZAZPuuMkHpnU8kQKBgQDr/bDGp503zUfSfUuSlg09ARw0CsUV8vzBAy4tibW6mB1BZc34OZ6bvzww/+yCr6t9oIJKCSp2u0O+g6waYQtxWkrAO4crC+D12mVHLCHuTY2Y6VGw3UfZa3dkf/rYHiuon2KxRvMSirdmfEEJpO9qZx9AvXIntQWckPTzgE/6kwKBgQDfcUpzBo1+RAdM3IGB9Cua+nkMHaJnUmuKVxZH1ViZzfD3xBymMW69bCyqiFOVIoKaC6NEXmQ3qeWvjUZN9cuG8dlWkjLJlP9SNNMARsRE52qLHsG3nW2HI8wPW0EoZdF8qiJYgza10hdDlxz+CregtAsi0h9B/J2LXxbAU+oj+wKBgFvDzlWxH8VvIZqL9jMN/h/Wqqzh8zlRv08eeXpjrjLcq6OefrUjUrWlazZyjflTbg/vtjorzkNVFkai1O19BwIQ5jhR7YGjoNp5DiDa3GbZ6VGoiIeJxEKbM1X1Hgmj0b5EHBBrUmHHZwGHF5M0e5SYfOKjyBwAnCoBg/6byn3ZAoGAEZpQPi2W+gqL9K8ueLlusf/nh1/SSoeAt15TAAe7uioyQKKvixw72CpsfmbNBuO4HECsdRdml8gHs0PS9RNXHGNzNtG/tIfLcYN91/i7P55nk1wx8LAzT8EvM0qCIJec4FBa8lQr/Dj34jhGbXEUtFFayzx4f+9RzggIt9Akkv8CgYEA5EVrvJx5aUBpCD5ldmVOrAXtSNcgiaiSJJu48N6kgPK2iVWkYsLgkpdez1pVG1yl8t9p6UEjgXnFXSXwJB8kvRmaFZ/ef+xk08OSRBWVhYWivl5LoMwcJVje7RQeSiciVyuDgRT3NYDFjJCG4kLX0fbbsulHdWh71CdP2m2cyuc=";
    
    @BeforeClass
    public static void setup() throws NoSuchAlgorithmException, UnknownHostException {
    	//RSAKeyPairGenerator keyPairGenerator = new RSAKeyPairGenerator();
    	
    	// Base64 encoding the public and private keys to ease the sharing of these keys
    	//String pubKey = Base64.getEncoder().encodeToString(keyPairGenerator.getPublicKey().getEncoded());
    	//String privKey = Base64.getEncoder().encodeToString(keyPairGenerator.getPrivateKey().getEncoded());

        //PublicKey pubKey = keyPairGenerator.getPublicKey();
        //PrivateKey privKey = keyPairGenerator.getPrivateKey();
    	InetAddress ipAddress = InetAddress.getByName(address);
    	
//    	new MyServer(portNumber, privKey).start();
    	client = new MyClient(ipAddress, pubKey);
    }
    
    @Test
    public void testCorrectKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {	
    	String[] correctKnockingSequence = {"5", "7000", "4000", "6543"};       
    	ArrayList<Integer> correctConnect = client.sendEcho(correctKnockingSequence);
    	//client.makeConnection(22);
    }
    
    /*
    @Test
    public void testIncorrectKnockSequence() throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {	
    	String[] incorrectKnockingSequence = {"5", "7000", "4010", "6543"};       
    	ArrayList<Integer> incorrectConnect = client.sendEcho(incorrectKnockingSequence);
    	client.makeConnection(incorrectConnect);
    }
    */
    
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
    
    /* 
    // This block of code is used to test late delivery packets
    // MyClient.java method needs to be slightly modified to test this code
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
 
    @AfterClass
    public static void tearDown() {
    	// ToDo
    }
}
