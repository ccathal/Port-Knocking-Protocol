# Port-Knocking-Protocol
4th Year Final Year Project on the design, implementation and testing of a Port Knocking Protocol.

## Port Knocking Title
𝑁 UDP packet sequence Port Knocking Protocol using ‘Encrypt-Then-Sign’ RSA-AES Hybrid Encryption and Digital Signature with client-server connection of 𝑁 random ports.

**Features** of the Port Knocking Protocol include:
  - The protocol is fully written in Java using Java Sockets
  - PK sequence sent over UDP
  - Implementation of RSA with AES Hybrid Encryption
  - Encrypt-then-Sign RSA Digital Signature for packet authentication
  - Packet capture using TCPDUMP
  - Simple GUI (Java Swing) and command line options available
  - Integrated with Linux IPTABLES
  - Packet routing of random connection ports to the PK connection port server-side hiding the PK connection port from eavesdroppers
  - Use of Java ThreadPools to execute IPTABLES commands which enable client-server connection through random connection ports.

The report discusses previous research on the PK protocol, other implemented protocols available, implementation of the above protocol, testing and conclusions. If only interested in further explaination of the above protocol or how to setup the protocol, Chapter 4 gives an in dept description of the full protocol details and implementation.
