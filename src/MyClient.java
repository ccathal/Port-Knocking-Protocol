import java.io.*;
import java.math.BigInteger;
import java.net.*;

public class MyClient {

	private DatagramSocket socket;
    private InetAddress address;
    private byte[] buf;

    public MyClient() {
    	try {
			socket = new DatagramSocket();
		} catch (SocketException e) {
			e.printStackTrace();
		}
        try {
			address = InetAddress.getByName("localhost");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		}
    }
 
    public String sendEcho(String[] knockingSequence, int portNumber) {
        
    	//send UDP packet
    	for (int i = 0; i < knockingSequence.length; i++) {
    		buf = knockingSequence[i].getBytes();
    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
    	    try {
    	    	socket.send(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	byte[] buff = new byte[256];
    	DatagramPacket recievePacket = new DatagramPacket(buff, buff.length);
        try {
			socket.receive(recievePacket);
		} catch (IOException e) {
			e.printStackTrace();
		}
        String received = new String(recievePacket.getData(), 0, recievePacket.getLength());
        System.out.println("client: " + received);
        return received;
    }
 
    public void close() {
        socket.close();
    }
}