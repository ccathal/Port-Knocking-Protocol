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
import java.util.Scanner;

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
	private static final String serverPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzfpsmXHMJvbJZf2eAeesQXzbABl9cpMFT+vkrdoEsRaK8GaprjOsZC32aM4HZinW/S2jmXNeKBhnfPGsNgT3dPRTMaLH9VAh2GsGVms98PLvVTPBmZFloo5nTacPqQWgm8qkPoZfUOyuEqodsfkf0CQK5I84n0GsorEmNNbDlLeA66OcgPto/2LBMqZAPTwGPanHDEpcEB11IkcsdHHmEBLvuyjSvQnORpJJc33SU/L0XyR/MZndwsgUAHxj8SvJ4kfgIpdBcBZiiHEZVjFOdre9YhQ4dTbQDBWODXUsfXNpbWBECihQbC8adwdtVQA2oLDorHpna10V3DzicQrHIQIDAQAB";
	private static final String clientPrivKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCr2dXGetuwg5Tmr23pUbiGIr9EU7g1y4Vwjgz2RHEDcR071e7kf6Sh7/fUwOPrqxRDnSxrJSn+7kQOaBcSgG3nBoblTiOnulTIsVKzqDGYVrYzWqe6sW/Lgu6e6LcK7eudT/oyIvsg/nscIdrvoB6Lxs7Anbnl0DJK2a28SqJly2zZVlST6xg3UnAYERXJepOnY0fTJt1jUalhpHgRmesHz7n7SGWzRd1Yadb+WjfY86XKs6PZ3uwkSPykTP9YRSMo42JHsGBHXczQb2tSFCqVHJFD2oUjvx5DRosme2dvxkwt9cHEzJqz7aHucLeIxzkra0rteJxtnA2qj1GB33jDAgMBAAECggEABdWadWecZ8y/1GkmUSKOIjexyK8S9PiY6i5YcE/4ikZf2LU1rBpNS5YrEtUNp+WiBX2jkWusllQeiWKtTdqAYLSPBd/2Q+6GjF3A0gqmzGY9P1cXlLYLdpxWX5zFAiD7u9aIRwV7Ay7AnT7lAPetsc8pG87/DbdYzcPAK0FZbB1CdcT1N94g4o3QI/3cybDRe+Z57M6DSAYe+1ogRikAqD7QLD2pnLU+ypVspR84Lq+LbLh+2D+hnZ+hiW52s5khzR24A5OOHUNzq7eAfl+tOhLkyNExpEqB79DnUkBPd8ttT2nd0EY3tIPSPfmj8qSFO7aypccz06jPe7Dj0q4RCQKBgQD7auN9gML1xZKdUEWv4CirDh9qlTudvOxnRAIOYdJtpPqB2GIA+6sCFNfWBsgGjmhYxsm+irgcQCxXkRcmExwd8fVfy37d9jwpZ4f/abhmxPx6rJHeK8jGXfPqFgnXWC2lUByla8cND6egss/1D6Oo4WZ+FQVIff9cDnXgHuQ8PQKBgQCu+7CC/SQi03d8+JP3ZNIPopbW/wxCdvIzkHK1IWxFcLNYOxSptVH37r/FEzM8dml2LMSNb4rtM3jmw9JPT4jQ81T4U4NZfisUmwxyBRpYCLgYAUDGjaIYk+Gg0PmXcZUSsU0PrOThNAh6S6JhGmTQlB9zn0jq59R6yQsd58TY/wKBgDIOPeymzioQg/g+GyHBB1fHIAogXBYznv2QVLh4UWTzC8z+P8TiVlG7xJ0gDIMBp1TFfzUoeS9mt21Xvbwe4eI0Yh4IbeHTPHch3bnEWqpbXckuwnvxS6/y1LUuXhc5vxzCrnFg2+iFzWH5N94alwLwnW9M0Bh2vXieiGluvRIhAoGAFeY1/w7DWQByMdfCXPHnQEGu8xVUaXUNdtqbIIQgUsh6CY7LVTn2GjttELMIdIa1SC7uIm1VS75nYSocxgREMTJi7fk1tRuPNLL66cItu9rLf2WYv8C2CrFnSYMd2ZBDgeViqZWPx4eFkBirJ8/v0hLXpNbIf22oL/29QG7jOe0CgYEAlPogHkE+5Q7OfJwabc56PMkNSm2ruFQ7WqLxie0jUXnIDYxr6TUFnrwpAmrwNmsZ+l8NOSjgHwJhb9taJBNGbtXWieNx0TcjeVRoXssV5BKksT2v4PLbjS3Vl/gBiIX3iszLTQBj7QJLNWT1VPKGMA2Buw57gtBZKyC8lQ4LVyc=";

	// main method to send prot knocking sequence via command line
	public static void main(String args[]) {

		InetAddressValidator validAddr = new InetAddressValidator();
		int portSequenceSize = 0;
		String serverIP = "";
		InetAddress serverAddr = null;
		
		// validate user inputs
		try {
			portSequenceSize = Integer.parseInt(args[0]);
			serverIP = args[1];
			if (!(portSequenceSize > 2) || !validAddr.isValid(serverIP)) {
				throw new Exception();
			} else {
				serverAddr = InetAddress.getByName(serverIP);
			}
		// print error message for invalid inputs
		} catch (Exception ex) {
			System.out.println(
					"Error: Run Port Knocking Client: java src.MyClient <port_sequence_size> <server_ip_address>");
			System.exit(0);
		}

		// ask user to input each port for port knocking sequence & validate input
		Scanner in = new Scanner(System.in);
		ArrayList<Integer> knockSequence = new ArrayList<Integer>();
		for (int j = 0; j < portSequenceSize; j++) {
			System.out.println("Enter port number " + (j + 1) + " of sequence: ");
			try {
				int port = in.nextInt();
				knockSequence.add(port);
			// print error message for invalid inputs
			} catch (Exception e) {
				System.out.println("Not a valid port number between 0 - 65535");
				System.out.println(
						"Error: Run Port Knocking Client: java src.MyClient <port_sequence_size> <server_ip_address>");
				in.close();
				System.exit(0);
			}
		}
		in.close();
		// send port knocking sequence
		new MyClient(serverAddr, knockSequence, serverPubKey, clientPrivKey);
		System.out.println("\nPort Knocking Sequence Successfully Sent!");
	}

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
	public ArrayList<Integer> sendEcho(ArrayList<Integer> knockSequence)
			throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException,
			NoSuchAlgorithmException, InterruptedException {

		// get list of random ports of length 'knockingsequence' to be used as sequence
		// of connection ports
		ArrayList<Integer> connectionPorts = getRandomConnectionSockets(knockSequence.size());

		// for each element in knockingSequence send a pack as an attempt knock to
		// server
		for (int i = 0; i < knockSequence.size(); i++) {

			// get timestamp from ntp
			long time = System.currentTimeMillis();

			// message to be encrypted
			// incl. ntp timestamp, random connection port, prepend server address
			// (authentication purpose), prepend client address (authentication purpose)
			String plainText = null;
			try {
				plainText = time + "," + connectionPorts.get(i) + "," + this.address.getHostAddress() + ","
						+ InetAddress.getLocalHost().getHostAddress();
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
	public ArrayList<Integer> sendOutOfOrderEcho(Object[] knockingSequenceP)
			throws InvalidKeyException, BadPaddingException, IllegalBlockSizeException, NoSuchPaddingException,
			NoSuchAlgorithmException, InterruptedException {

		// get list of random ports of length 'knockingsequence' to be used as sequence
		// of connection ports
		ArrayList<Integer> connectionPorts = getRandomConnectionSockets(knockingSequenceP.length);

		// for each element in knockingSequence send a pack as an attempt knock to
		// server
		for (int i = 0; i < knockingSequenceP.length - 1; i++) {

			// get timestamp from ntp
			long time = (long) knockingSequenceP[1];

			// message to be encrypted
			// incl. ntp timestamp, random connection port, prepend server address
			// (authentication purpose), prepend client address (authentication purpose)
			String plainText = null;
			try {
				plainText = time + "," + connectionPorts.get(i) + "," + this.address.getHostAddress() + ","
						+ InetAddress.getLocalHost().getHostAddress();
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

			// send packet
			this.buf = sendPacket.getBytes();
			DatagramPacket packet = new DatagramPacket(this.buf, this.buf.length, this.address,
					Integer.parseInt((String) knockingSequenceP[0]));
			try {
				socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return connectionPorts;
	}

	// method to return number of random port numbers up to 65535. Will act as
	// connection sockets.
	private ArrayList<Integer> getRandomConnectionSockets(int packedSequenceSize) {
		ArrayList<Integer> sock = new ArrayList<>();
		for (int i = 0; i < packedSequenceSize; i++) {
			sock.add(r.nextInt(65535));
		}
		return sock;
	}

	private String signFile(String text, String privKey)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
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
	public void makeConnection(ArrayList<Integer> correctConnectArr) throws InvalidKeyException, BadPaddingException,
			IllegalBlockSizeException, NoSuchPaddingException, NoSuchAlgorithmException, InterruptedException {
		// loop through each integer in arraylist that represents a connection to server
		for (int correctConnect : correctConnectArr) {
			Thread.sleep(1000);
			// get current time in seconds
			long start = System.currentTimeMillis() / 1000;
			// allow connection for 10 seconds before moving to next connection port
			while (System.currentTimeMillis() / 1000 - start <= 10) {

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
