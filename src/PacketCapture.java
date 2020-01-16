package src;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.jnetpcap.Pcap;
import org.jnetpcap.PcapIf;
import org.jnetpcap.packet.PcapPacket;
import org.jnetpcap.packet.PcapPacketHandler;
import org.jnetpcap.protocol.network.Arp;
 
public class PacketCapture {
 
    public static void main(String[] args) {
        try {
            // Will be filled with NICs
            List<PcapIf> alldevs = new ArrayList<PcapIf>();
 
            // For any error msgs
            StringBuilder errbuf = new StringBuilder();
 
            //Getting a list of devices
            int r = Pcap.findAllDevs(alldevs, errbuf);
            System.out.println(r);
            if (r != Pcap.OK) {
                System.err.printf("Can't read list of devices, error is %s", errbuf
                        .toString());
                return;
            }
 
            System.out.println("Network devices found:");
            int i = 0;
            for (PcapIf device : alldevs) {
                String description =
                        (device.getDescription() != null) ? device.getDescription()
                        : "No description available";
                System.out.printf("#%d: %s [%s]\n", i++, device.getName(), description);
            }
            System.out.println("choose the one device from above list of devices");
            int ch = new Scanner(System.in).nextInt();
            PcapIf device = alldevs.get(ch);
 
            int snaplen = 64 * 1024;           // Capture all packets, no trucation
            int flags = Pcap.MODE_PROMISCUOUS; // capture all packets
            int timeout = 10 * 1000;           // 10 seconds in millis
 
            //Open the selected device to capture packets
            Pcap pcap = Pcap.openLive(device.getName(), snaplen, flags, timeout, errbuf);
 
            if (pcap == null) {
                System.err.printf("Error while opening device for capture: "
                        + errbuf.toString());
                return;
            }
            System.out.println("device opened");
 
            //Create packet handler which will receive packets
            PcapPacketHandler jpacketHandler = new PcapPacketHandler() {
                Arp arp = new Arp();
 
                @Override
                public void nextPacket(PcapPacket packet, Object user) {
                    //Here i am capturing the ARP packets only,you can capture any packet that you want by just changing the below if condition
                    if (packet.hasHeader(arp)) {
                        System.out.println("Hardware type" + arp.hardwareType());
                        System.out.println("Protocol type" + arp.protocolType());
                        System.out.println("Packet:" + arp.getPacket());
                        System.out.println();
                    }
                }
            };
            //we enter the loop and capture the 10 packets here.You can  capture any number of packets just by changing the first argument to pcap.loop() function below
            pcap.loop(10, jpacketHandler, "jnetpcap rocks!");
            //Close the pcap
            pcap.close();
        } catch (Exception ex) {
            System.out.println(ex);
        }
    }
}

//import org.pcap4j.core.NotOpenException;
//import org.pcap4j.core.PacketListener;
//import org.pcap4j.core.PcapDumper;
//import org.pcap4j.core.PcapHandle;
//import org.pcap4j.core.PcapNativeException;
//import org.pcap4j.core.PcapNetworkInterface;
//import org.pcap4j.core.PcapStat;
//
//import java.io.IOException;
//
//import org.pcap4j.core.BpfProgram.BpfCompileMode;
//import org.pcap4j.core.PcapNetworkInterface.PromiscuousMode;
//import org.pcap4j.packet.Packet;
//import org.pcap4j.util.NifSelector;
//
//public class PacketCapture {
//
//    static PcapNetworkInterface getNetworkDevice() {
//        PcapNetworkInterface device = null;
//        try {
//            device = new NifSelector().selectNetworkInterface();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return device;
//    }
//
//    public static void main(String[] args) throws PcapNativeException, NotOpenException {
//        // The code we had before
//        PcapNetworkInterface device = getNetworkDevice();
//        System.out.println("You chose: " + device);
//
//        // New code below here
//        if (device == null) {
//            System.out.println("No device chosen.");
//            System.exit(1);
//        }
//
//        // Open the device and get a handle
//        int snapshotLength = 65536; // in bytes   
//        int readTimeout = 50; // in milliseconds                   
//        final PcapHandle handle;
//        handle = device.openLive(snapshotLength, PromiscuousMode.PROMISCUOUS, readTimeout);
//        PcapDumper dumper = handle.dumpOpen("out.pcap");
//
//        // Set a filter to only listen for tcp packets on port 80 (HTTP)
//        String filter = "udp port 4445";
//        handle.setFilter(filter, BpfCompileMode.OPTIMIZE);
//
//        // Create a listener that defines what to do with the received packets
//        PacketListener listener = new PacketListener() {
//            @Override
//            public void gotPacket(Packet packet) {
//                // Print packet information to screen
//                System.out.println(handle.getTimestamp());
//                System.out.println(packet);
//
//                // Dump packets to file
//                try {
//                    dumper.dump(packet, handle.getTimestamp());
//                } catch (NotOpenException e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//
//        // Tell the handle to loop using the listener we created
//        try {
//            int maxPackets = 50;
//            handle.loop(maxPackets, listener);
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//
//        // Print out handle statistics
//        PcapStat stats = handle.getStats();
//        System.out.println("Packets received: " + stats.getNumPacketsReceived());
//        System.out.println("Packets dropped: " + stats.getNumPacketsDropped());
//        System.out.println("Packets dropped by interface: " + stats.getNumPacketsDroppedByIf());
//        // Supported by WinPcap only
//        String osName = System.getProperty("os.name");
//        if (osName.startsWith("Windows")) {
//            System.out.println("Packets captured: " +stats.getNumPacketsCaptured());
//        }
//
//        // Cleanup when complete
//        dumper.close();
//        handle.close();
//    }
//
//}
