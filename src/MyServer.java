import java.io.*;
import java.net.*;
import java.util.ArrayList;

public class MyServer extends Thread {
    
	private DatagramSocket socket;
    private boolean running;
    private byte[] buf = new byte[256];
    private ArrayList<String> knockingSequence = new ArrayList<String>();
    private final String[] confirmKnockingSequence = {"5", "7000", "4000", "6543"};
 
    public MyServer(int pn) {
        try {
			socket = new DatagramSocket(pn);
		} catch (SocketException e) {
			e.printStackTrace();
		}
    }
 
    public void run() {
        running = true;
 
        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
				socket.receive(packet);
			} catch (IOException e) {
				e.printStackTrace();
			}         
            //InetAddress address = packet.getAddress();
            //int port = packet.getPort();
            //packet = new DatagramPacket(buf, buf.length, address, port);
            String received = new String(packet.getData(), 0, packet.getLength());
            System.out.println("server: message = " + received + ".");
            
            if (knockingSequence.size() < 5) {
            	
            	knockingSequence.add(received);
            	
            	if(knockingSequence.size() == 4) {
	            	for (int i = 0; i < 4; i++) {
	            		System.out.println("" + knockingSequence.get(i) + " = " + confirmKnockingSequence[i]);
	            		if (!knockingSequence.get(i).equals(confirmKnockingSequence[i])) {
	            			System.out.println("unsucessful - closing connection");
	            			running = false;
	            			break;
	            		}
	            	}
            	}
            }
        }     
        socket.close();
    }
}