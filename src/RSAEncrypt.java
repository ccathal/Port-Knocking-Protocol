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

public class RSAEncrypt {
	
	private static String publicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCOnXJ+5YPExU7cWyKHfaNJ+lJ09TpzRdPwW6ybUhQuT6NgJ+OTfq/rB6wPV2/9rLXOhOODLip3l516GPY1j6G8b+9ikLc+mUs7URvKEc+XnBbMIqOZ/0ZtFr7EbVlblSm1nIZxOVo0NxoyEByJ2TGj6v4rdxbnPdAhl14GJ2e0MQIDAQAB";
    private static String privateKey = "MIICdwIBADANBgkqhkiG9w0BAQEFAASCAmEwggJdAgEAAoGBAI6dcn7lg8TFTtxbIod9o0n6UnT1OnNF0/BbrJtSFC5Po2An45N+r+sHrA9Xb/2stc6E44MuKneXnXoY9jWPobxv72KQtz6ZSztRG8oRz5ecFswio5n/Rm0WvsRtWVuVKbWchnE5WjQ3GjIQHInZMaPq/it3Fuc90CGXXgYnZ7QxAgMBAAECgYAJxUu02fCfKI1wl2XUNo1bYiUIEk0A3MXab81xjrwHODmPOuxijIls5seo/WqUzKJgFcaolB8gtmh5lwA3RzHjZuyeT3rcbyzTPaSMaPek5AhfyHKXODF4FXiIF45/Y+aYqmhE+sCOf5JOOy8eYf6mnT9/FxeJ/bRz5ToBFgixaQJBAMZaAEsGYWz9C/1hh3uSKk3CtDqtzhOJZy4pvwmidJ65vu7ID9T3QwqBYDZgDq9HEcK+0qkVX6GqY1Wq+Y7q5lcCQQC4EHeXKAgL9nifV57AbauV3jIWHxhG7I7roDiVoGJSWOWhfTCHTnWOnjDA6h5HZDUdEOq7J6sd473uzfFXC9S3AkB972LEP7bXxgo4xBWLJZBqcraPkw6GZPT34FWEvnqg1HofjTJQGvWb0+zMWUy5iLwEE7gY1pMzR8Vt/PIaBZZdAkEAoyP9S11w3GGFgf2gOIA77+Zz0EWZN+udtVxaKsxvuPsP9LVIGPVCnri5D5OyaKZ5qRyAYIeFVEWx54JjYTqcOwJBALvI0+wzR/4GzPC5UrOZp8lNxqSBI+GukZrJ36bUYHgmi3A8DpGsMYWX4AbjA5ZyglLzuqVNvcGMUnhuG+ibtms=";

	
	public static PublicKey getPublicKey(String base64PublicKey){
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

    public static byte[] encrypt(String data) throws BadPaddingException, IllegalBlockSizeException, InvalidKeyException, NoSuchPaddingException, NoSuchAlgorithmException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, getPublicKey(publicKey));
        return cipher.doFinal(data.getBytes());
    }

    public static String decrypt(byte[] data, PrivateKey privateKey) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return new String(cipher.doFinal(data));
    }

    public static String decrypt(String data) throws IllegalBlockSizeException, InvalidKeyException, BadPaddingException, NoSuchAlgorithmException, NoSuchPaddingException {
        return decrypt(Base64.getDecoder().decode(data.getBytes()), getPrivateKey(privateKey));
    }
}
