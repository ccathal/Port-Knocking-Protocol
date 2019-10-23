import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;

public class MyServer extends Thread {
    
	private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private ArrayList<String> knockingSequence = new ArrayList<String>();
    private final ArrayList<String> confirmKnockingSequence =  new ArrayList<String>(Arrays.asList("5", "7000", "4000", "6543"));
 
    public MyServer(int pn) {
        try {
			socket = new DatagramSocket(pn);
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }
 
    public void run() {
        running = true;
        System.out.println("Knocking Sequence: " + confirmKnockingSequence);
 
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}         
            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("server: knocking sequence recieved = " + received + ".");
            
            if (knockingSequence.size() < 5) {
            	
            	knockingSequence.add(received);
            	            	
            	if(knockingSequence.size() == 4) {
            		String response;
            		if (knockingSequence.equals(confirmKnockingSequence)) {
            			response = "success";
            		} else {
            			response = "unsucessful - closing connection";
    					running = false;
            		}
            		knockingSequence.clear();
            		buf = response.getBytes();
            		packet = new DatagramPacket(buf, buf.length, address, port);
            		try {
						socket.send(packet);
					} catch (IOException e) {
						e.printStackTrace();
					}
            	}          	
            }
        }
        socket.close();
    }
}