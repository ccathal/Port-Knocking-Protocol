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
 
    public void sendEcho(String[] knockingSequence, int portNumber) {
        
    	//send UDP packet
    	for (int i = 0; i <= knockingSequence.length; i++) {
    		buf = knockingSequence[i].getBytes();
    		//buf = BigInteger.valueOf(knockingSequence[i]).toByteArray();
    	    DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
    	    try {
    	    	socket.send(packet);
//    	    	try {
//					Thread.sleep(1000);
//				} catch (InterruptedException e) {
//					e.printStackTrace();
//				}
			} catch (IOException e) {
				e.printStackTrace();
			}
    	} 
    }
 
    public void close() {
        socket.close();
    }
}