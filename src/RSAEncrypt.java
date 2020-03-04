package src;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class RSAEncrypt {
	
	// generate public key
	// have base64 encoded public key
	// first Base64 decode and generate the public key
	// need X509EncodedKeySpec class to convert it again to RSA public key
	// needed if publickey is converted to base64 (String)
	public static PublicKey getPublicKey(String base64PublicKey) {
        PublicKey publicKey = null;
        try{
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.getDecoder().decode(base64PublicKey.getBytes()));
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            publicKey = keyFactory.generatePublic(keySpec);
            return publicKey;
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return publicKey;
    }

	// generate private key
    // private key is generated in PKCS#8 format
    // generate the private key from base64 encoded string using PKCS8EncodedKeySpec
	// ***** currently not used : only needed if privatekey is converted to base64 (String)
    public static PrivateKey getPrivateKey(String base64PrivateKey){
        PrivateKey privateKey = null;
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(Base64.getDecoder().decode(base64PrivateKey.getBytes()));
        KeyFactory keyFactory = null;
        try {
            keyFactory = KeyFactory.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        try {
            privateKey = keyFactory.generatePrivate(keySpec);
        } catch (InvalidKeySpecException e) {
            e.printStackTrace();
        }
        return privateKey;
    }

    // encryption method
    // takes the string to be enrypted and the Base64 encoded RSA key for encryption
    public static byte[] encrypt(String publicKey, SecretKey secKey) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(secKey.getEncoded());
    }

    // decryption methods
    // decrypt method that accepts RSA encrypted string and Base64 encoded RSA private key for decryption
    public static byte[] decrypt(String privateKey, String data) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
    	Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
    	cipher.init(Cipher.DECRYPT_MODE, getPrivateKey(privateKey));
    	//return cipher.doFinal(Base64.getDecoder().decode(data.getBytes()));
    	return cipher.doFinal(Base64.getDecoder().decode(data.getBytes()));
    }
}
