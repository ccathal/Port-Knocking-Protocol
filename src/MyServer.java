package src;

import java.io.*;
import java.net.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.security.SignatureException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import static java.nio.charset.StandardCharsets.UTF_8;

public class MyServer extends Thread {

	private int confirmKnockingPort;
	private ArrayList<Integer> confirmKnockingSequence = new ArrayList<Integer>();
	private String tcpDumpCommand;
	private FileHandler fh;
	private Logger logger = Logger.getLogger("MyLog");
	private HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>> hashKnock = new HashMap<AttemptKnockingSequence, ArrayList<SingleKnock>>();
	private final String serverPrivKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDN+myZccwm9sll/Z4B56xBfNsAGX1ykwVP6+St2gSxForwZqmuM6xkLfZozgdmKdb9LaOZc14oGGd88aw2BPd09FMxosf1UCHYawZWaz3w8u9VM8GZkWWijmdNpw+pBaCbyqQ+hl9Q7K4Sqh2x+R/QJArkjzifQayisSY01sOUt4Dro5yA+2j/YsEypkA9PAY9qccMSlwQHXUiRyx0ceYQEu+7KNK9Cc5GkklzfdJT8vRfJH8xmd3CyBQAfGPxK8niR+Ail0FwFmKIcRlWMU52t71iFDh1NtAMFY4NdSx9c2ltYEQKKFBsLxp3B21VADagsOisemdrXRXcPOJxCschAgMBAAECggEATLM3xVvOvaOgE06BjAwM5MXtdvgG8qc0jzI0EVjh7l+KlUJlZOzxAMzsuNIfhzeFSvm3tehz41JTFv+XNPZcfzyLzivjccHJGKGh1oRQqGyOGpgPO3Qc+I82gH/5IONrjxfCWVYIIEZB+8lFDYTLB+Kj+8ApQYRfGKYGqB5g7ftL/YNmaUI1ntnIpssXrq08iHbEVA+jDrCvQj9glaJiF4Fxylvzd6Qep2Kh/7sYvEHmPPEaEOJhXOc6uxwSiMGU2eexjNxrQa3P5dv2rEDHgZArNoye5ZVVQpGqF5GagmY7sW5tHm9VkwRCVZo6s3uk7S/OWZmZAZPuuMkHpnU8kQKBgQDr/bDGp503zUfSfUuSlg09ARw0CsUV8vzBAy4tibW6mB1BZc34OZ6bvzww/+yCr6t9oIJKCSp2u0O+g6waYQtxWkrAO4crC+D12mVHLCHuTY2Y6VGw3UfZa3dkf/rYHiuon2KxRvMSirdmfEEJpO9qZx9AvXIntQWckPTzgE/6kwKBgQDfcUpzBo1+RAdM3IGB9Cua+nkMHaJnUmuKVxZH1ViZzfD3xBymMW69bCyqiFOVIoKaC6NEXmQ3qeWvjUZN9cuG8dlWkjLJlP9SNNMARsRE52qLHsG3nW2HI8wPW0EoZdF8qiJYgza10hdDlxz+CregtAsi0h9B/J2LXxbAU+oj+wKBgFvDzlWxH8VvIZqL9jMN/h/Wqqzh8zlRv08eeXpjrjLcq6OefrUjUrWlazZyjflTbg/vtjorzkNVFkai1O19BwIQ5jhR7YGjoNp5DiDa3GbZ6VGoiIeJxEKbM1X1Hgmj0b5EHBBrUmHHZwGHF5M0e5SYfOKjyBwAnCoBg/6byn3ZAoGAEZpQPi2W+gqL9K8ueLlusf/nh1/SSoeAt15TAAe7uioyQKKvixw72CpsfmbNBuO4HECsdRdml8gHs0PS9RNXHGNzNtG/tIfLcYN91/i7P55nk1wx8LAzT8EvM0qCIJec4FBa8lQr/Dj34jhGbXEUtFFayzx4f+9RzggIt9Akkv8CgYEA5EVrvJx5aUBpCD5ldmVOrAXtSNcgiaiSJJu48N6kgPK2iVWkYsLgkpdez1pVG1yl8t9p6UEjgXnFXSXwJB8kvRmaFZ/ef+xk08OSRBWVhYWivl5LoMwcJVje7RQeSiciVyuDgRT3NYDFjJCG4kLX0fbbsulHdWh71CdP2m2cyuc=";
	private final String clientPubKey1 = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAq9nVxnrbsIOU5q9t6VG4hiK/RFO4NcuFcI4M9kRxA3EdO9Xu5H+koe/31MDj66sUQ50sayUp/u5EDmgXEoBt5waG5U4jp7pUyLFSs6gxmFa2M1qnurFvy4Lunui3Cu3rnU/6MiL7IP57HCHa76Aei8bOwJ255dAyStmtvEqiZcts2VZUk+sYN1JwGBEVyXqTp2NH0ybdY1GpYaR4EZnrB8+5+0hls0XdWGnW/lo32POlyrOj2d7sJEj8pEz/WEUjKONiR7BgR13M0G9rUhQqlRyRQ9qFI78eQ0aLJntnb8ZMLfXBxMyas+2h7nC3iMc5K2tK7XicbZwNqo9Rgd94wwIDAQAB";
	private HashMap<InetAddress, String> keyManagment = new HashMap<>();

	// main method to start server via command line
	public static void main(String args[]) throws IOException, InterruptedException {

		int portSequenceSize = 0;
		// validate user inputs of port knocking sequence size
		try {
			portSequenceSize = Integer.parseInt(args[0]);
			if (!(portSequenceSize > 2)) {
				throw new Exception();
			}
			// print error message for invalid inputs
		} catch (Exception ex) {
			System.out.println("Error: Run Port Knocking Client: java src.MyServer <port_sequence_size>");
			System.exit(0);
		}

		// ask user to input each port for port knocking sequence & validate input
		ArrayList<Integer> knockSequence = new ArrayList<Integer>();
		int destPort = 0;
		Scanner in = new Scanner(System.in);
		try {
			for (int j = 0; j < portSequenceSize; j++) {
				System.out.println("Enter port number " + (j + 1) + " of sequence: ");
				int port = in.nextInt();
				knockSequence.add(port);
				// print error message for invalid inputs
			}
			System.out.println("Enter destination port number of sequence: ");
			destPort = in.nextInt();
			in.close();
			if (!(destPort > 2)) {
				throw new Exception();
			}
			// print error message for invalid inputs
		} catch (Exception e) {
			System.out.println("Not a valid port number between 0 - 65535");
			System.out.println(
					"Error: Run Port Knocking Client: java src.Server <port_sequence_size> <server_ip_address>");
			in.close();
			System.exit(0);
		}

		// start server with user inputs
		System.out.println("\nPort Knocking Server Successfully Started!\n");
		MyServer server = new MyServer(destPort, knockSequence);
		server.runTCPDUmp();
	}

	public MyServer(int knockPort, ArrayList<Integer> knockSequence) {
		this.confirmKnockingPort = knockPort;
		this.confirmKnockingSequence = knockSequence;
		String command = "/usr/sbin/tcpdump -A -l -n udp port '(";
		for (int i = 0; i < confirmKnockingSequence.size(); i++) {
			if (i == confirmKnockingSequence.size() - 1) {
				command += "" + confirmKnockingSequence.get(i) + ")'";
			} else
				command += "" + confirmKnockingSequence.get(i) + " or ";
		}
		this.tcpDumpCommand = command;
		try {
			this.keyManagment.put(InetAddress.getByName("192.168.56.1"), clientPubKey1);
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
	}

	public void runTCPDUmp() throws IOException, InterruptedException {

		// set up logging file
		try {
			fh = new FileHandler("MyKnockLogFile.log");
		} catch (SecurityException | IOException e2) {
			e2.printStackTrace();
		}

		logger.addHandler(fh);
		SimpleFormatter formatter = new SimpleFormatter();
		fh.setFormatter(formatter);

		logger.info("Desired Knock Sequence - " + confirmKnockingSequence + " : Corresponding Knock Port - "
				+ confirmKnockingPort);

		// setup process builder for running tcpdump command for Linux machine
		ProcessBuilder tcpDumpProcessBuilder = new ProcessBuilder("/bin/bash", "-c", tcpDumpCommand);
		tcpDumpProcessBuilder.redirectErrorStream(true);

		try {
			// start tcpdump
			Process process = tcpDumpProcessBuilder.start();

			// set up buffered and input stream reader for reading tcpdump & remove first 2
			// lines of tcpdump
			BufferedReader inputStreamReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			Thread.sleep(3000);
			inputStreamReader.readLine();
			inputStreamReader.readLine();
			String line = null;

			// start reading tcpdump from filtered knock ports
			while ((line = inputStreamReader.readLine()) != null) {

				// boolean which will be implemented if IllegalArgumentException is thrown by
				// failing to create a client or decryption error
				boolean parsingCheck = true;

				long arrivalTime = System.currentTimeMillis();

				// parse source and destination information from tcpdump
				String[] tcpArr = line.split(" ");

				int srcIndex = tcpArr[2].toString().lastIndexOf(".");
				String srcIP = tcpArr[2].toString().substring(0, srcIndex);
				String srcPort = tcpArr[2].toString().substring(srcIndex + 1);

				int destIndex = tcpArr[4].toString().lastIndexOf(".");
				String destIP = tcpArr[4].toString().substring(0, destIndex);
				String destPort = tcpArr[4].toString().substring(destIndex + 1, tcpArr[4].toString().length() - 1);

				// set up new clientKnockingSequence with client IP and Port if does not already
				// exist
				AttemptKnockingSequence aks = null;
				try {
					aks = new AttemptKnockingSequence(InetAddress.getByName(srcIP), Integer.parseInt(srcPort));
				} catch (IllegalArgumentException ex) {
					logger.warning("Failure to create client identity (parsing client IP and/or port): IP - "
							+ aks.getAddress() + ": Port - " + aks.getPort());
					parsingCheck = false;
				}

				ArrayList<SingleKnock> knockList = new ArrayList<>();
				// add the client knocking details to hashmap
				if (hashKnock.containsKey(aks)) {
					// if its over 30 seconds since previous packet is arrived, assume packet loss
					// and timeout occured
					if (!hashKnock.get(aks).isEmpty() && (arrivalTime / 1000
							- hashKnock.get(aks).get(hashKnock.get(aks).size() - 1).getArrivalTime() / 1000 > 5.0)) {
						logger.warning("Late knock packets from: IP - " + srcIP + ": Port - " + srcPort
								+ ". Starting port knocking from specified IP address assuming packet loss & timeout occured");
						hashKnock.get(aks).clear();
					}
					logger.info("Multiple knock packets from: IP - " + srcIP + ": Port - " + srcPort);
				} else {
					// if client details does not exist yet -> add to hashmap
					hashKnock.put(aks, knockList);
					// these values set to false which represent a successful connection and a
					// renewal attempt
					aks.setRenewingConnection(false);
					aks.setSubmittedConnection(false);
				}

				String values[] = null;
				String dumpPacketData = inputStreamReader.readLine();
				int splitIndex = dumpPacketData.lastIndexOf(";");
				String[] signatureSplit = { dumpPacketData.substring(0, splitIndex),
						dumpPacketData.substring(splitIndex + 1) };

				// check if the client has submitted their public key to the server to
				// authenticate itself
				if (this.keyManagment.containsKey(InetAddress.getByName(srcIP))) {

					String text = "";
					String sign = "";
					try {
						text = new String(signatureSplit[1].toString().getBytes(), 0,
								signatureSplit[1].toString().getBytes().length);
						sign = new String(signatureSplit[0].toString().substring(28).getBytes(), 0,
								signatureSplit[0].toString().substring(28).getBytes().length);
					} catch (Exception ex) {
						logger.info("Error parsing packets cipher text and signature text: IP - " + aks.getAddress()
								+ ": Port - " + aks.getPort());
						parsingCheck = false;
					}

					try {
						// server contains clients key but needs to verify signature
						if (parsingCheck && verify(text, sign, this.keyManagment.get(InetAddress.getByName(srcIP)))) {
							try {
								// client signature verified
								// decrypt aesKey using RSA private key
								// then, decrypt packet data using aesKey
								// parse decrypted packet data
								String[] packetData = text.split(" ");

								String aesKey = new String(packetData[1].toString().getBytes(), 0,
										packetData[1].toString().getBytes().length);
								byte[] decryptKey = RSAEncrypt.decrypt(serverPrivKey, aesKey);
								SecretKey origionalKey = new SecretKeySpec(decryptKey, 0, decryptKey.length, "AES");

								Cipher aesCipher = Cipher.getInstance("AES");
								aesCipher.init(Cipher.DECRYPT_MODE, origionalKey);
								byte[] bytePlainText = aesCipher
										.doFinal(Base64.getDecoder().decode(packetData[0].toString()));

								String plainText = new String(bytePlainText);
								values = plainText.split(",");

								// if(!values[2].equals(InetAddress.getLocalHost().getHostAddress()) ||
								// !values[3].equals(srcIP)) {
								if (!values[2].equals("192.168.56.101") || !values[3].equals(srcIP)) {
									logger.severe(
											"Server and/or client ID prepended to packet message have been modified by unauthorized user: IP - "
													+ aks.getAddress() + ": Port - " + aks.getPort());
									parsingCheck = false;
								}

								// deal with packet with ntp timestamp in future
								if (parsingCheck && Long.parseLong(values[0]) > arrivalTime) {
									logger.severe("Future time in Single Packet Knock: IP - " + aks.getAddress()
											+ ": Port - " + aks.getPort());
									hashKnock.get(aks).clear();
									parsingCheck = false;
								}

								if (parsingCheck && arrivalTime / 1000 - Long.parseLong(values[0]) / 1000 > 20.0) {
									logger.severe(
											"Delivery of packet took too long. Assuming packet modification: IP - "
													+ aks.getAddress() + ": Port - " + aks.getPort());
									hashKnock.get(aks).clear();
									parsingCheck = false;
								}

							} catch (IllegalArgumentException | BadPaddingException | NullPointerException
									| ArrayIndexOutOfBoundsException ex) {
								logger.warning("Failure decrypting client packet: IP - " + aks.getAddress()
										+ " : Port - " + aks.getPort());
								parsingCheck = false;
							}
						} else {
							logger.warning("Failure to authenticate client: IP - " + aks.getAddress() + " : Port - "
									+ aks.getPort());
							parsingCheck = false;
						}
					} catch (SignatureException ex) {
						logger.warning("Tampered or Incorrect Digital Signiture: IP - " + aks.getAddress()
								+ " : Port - " + aks.getPort());
						parsingCheck = false;
					}
				} else {
					logger.warning("Server does not contain a key for client: IP - " + aks.getAddress() + " : Port - "
							+ aks.getPort());
					parsingCheck = false;
				}

				// check if no IllegalArgumentException errors have occurred or future packets
				// sent
				if (parsingCheck) {

					logger.info("Single Knock Attempt ClientIP - " + aks.getAddress() + ": ClientPort - "
							+ aks.getPort() + ": Port Knock Entered - " + destPort);

					// print 'late packet arrival' message
					// when incoming packet ntp timestamp is greater than previous packet timestamp
					if (!(hashKnock.get(aks).size() == 0) && Long.parseLong(values[0]) < hashKnock.get(aks)
							.get(hashKnock.get(aks).size() - 1).getTime()) {
						logger.warning("Late packet arrival in Single Packet Knock: IP - " + aks.getAddress()
								+ ": Port - " + aks.getPort());
					}

					// get hashmap array of single knocks
					ArrayList<SingleKnock> arr = hashKnock.get(aks);

					// add incoming knock to the attempt
					SingleKnock single = null;
					try {
						single = new SingleKnock(Integer.parseInt(destPort), Long.parseLong(values[0]),
								Integer.parseInt(values[1]), arrivalTime);
					} catch (IllegalArgumentException ex) {
						System.out.println("XX");
					}

					// check if renewal connection attempt, clear current connection knock in
					// hashmap
					if (aks.getSubmittedConnection() && (hashKnock.get(aks).size() == confirmKnockingSequence.size())
							&& !aks.getRenewingConnection()) {
						hashKnock.get(aks).clear();
						aks.setRenewingConnection(true);
					}
					// adding single knock to hashmap of corresponding attempt knocking sequence
					hashKnock.computeIfAbsent(aks, k -> new ArrayList<>()).add(single);

					// if array size is now full
					if (arr.size() == confirmKnockingSequence.size()) {

						// sort single knocks of corresponding attempt knocking sequence based on ntp
						// timestamps in single knock packets
						Collections.sort(hashKnock.get(aks));

						// populate knock and connection sequences in the hashmap
						ArrayList<Integer> knockSequence = new ArrayList<Integer>();
						ArrayList<Integer> connectionSequence = new ArrayList<Integer>();
						for (SingleKnock sk : hashKnock.get(aks)) {
							knockSequence.add(sk.getPortKnock());
							connectionSequence.add(sk.getConnectionKnock());
						}

						// if knocking sequence matches, connection allowed
						if (knockSequence.equals(confirmKnockingSequence)) {
							logger.info(
									"Correct Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
							logger.info("Submitting connection ports for allowed connection: Connection Knocks - "
									+ connectionSequence);

							// if renewing connection attempt but submitted connection has expired
							if (!aks.getSubmittedConnection() && aks.getRenewingConnection()) {
								new Connection(aks, hashKnock.get(aks), confirmKnockingPort, logger, hashKnock);
								aks.setSubmittedConnection(true);
								aks.setRenewingConnection(false);
							}
							// if new connection attempt
							else if (!aks.getSubmittedConnection()) {
								new Connection(aks, hashKnock.get(aks), confirmKnockingPort, logger, hashKnock);
								aks.setSubmittedConnection(true);
								// renewing attempt will replace the current knocking attempt
							} else {
								aks.setRenewingConnection(false);
							}
						} else {
							// else, connection refused & hashmap cleared to corresponding attempt knocking
							// sequence
							logger.warning(
									"Incorrect Knock Sequence: IP - " + aks.getAddress() + ": Port - " + aks.getPort());
							hashKnock.get(aks).clear();
						}
					}
				}
			}
			// collect error streams
			BufferedReader errorStreamReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));

			while (line != null) {
				System.out.println(line);
			}

		} catch (Exception e) {
			StringWriter errors = new StringWriter();
			e.printStackTrace(new PrintWriter(errors));
			logger.warning("Error running tcpdump: " + errors.toString() + ". Continuing runnning.");
		}
	}

	// method to authenticate the client sending the knock packets
	// method verifies the clients signature of their private key on the encrypted
	// data
	public boolean verify(String text, String signature, String pubKey)
			throws NoSuchAlgorithmException, InvalidKeyException, SignatureException {
		Signature sign = Signature.getInstance("SHA256withRSA");
		sign.initVerify(RSAEncrypt.getPublicKey(pubKey));
		sign.update(text.getBytes(UTF_8));

		byte[] signatureBytes = Base64.getDecoder().decode(signature);
		return sign.verify(signatureBytes);
	}

}