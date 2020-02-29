package src;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;

public class ServerGui extends JFrame {

    private JFrame frame, mainFrame;
    private JPanel knock, numbers, listing, port, buttonPanel;
    private JLabel knockLabel, portLabel, listingLabel;
    private ArrayList<JTextField> portSequence = new ArrayList<JTextField>();
    private String sequenceNumber;
    private JTextField portBox;

    public ServerGui() {
        
    	//*** FRAME 1
    	frame = new JFrame("Server");
    	frame.setPreferredSize(new Dimension(425, 310));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        JPanel content = new JPanel(new BorderLayout());
        
        knock = new JPanel();
        knock.setLayout(new BoxLayout(knock, BoxLayout.Y_AXIS));
        knock.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        knockLabel = new JLabel("Choose Knock Sequence Length");
        knockLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        knock.add(knockLabel);
        
        numbers = new JPanel();
        numbers.setLayout(new GridLayout(2, 2, 30, 30));
        for (int i = 3; i < 7; i++) {
        	JButton button = new JButton("" + i);
        	button.setPreferredSize(new Dimension(80, 90));
        	button.setFont(new Font("Arial", Font.PLAIN, 23));
        	numbers.add(button);
        	button.addActionListener(new ActionListener() {
    			@Override
    			public void actionPerformed(ActionEvent arg0) {
    				frame.setVisible(false);
					frame.dispose();
					
    				sequenceNumber = button.getText();
    				
    				// FRAME 2
    				mainFrame = new JFrame("Server");
    				mainFrame.setLayout(new BorderLayout());
    				mainFrame.addWindowListener(new WindowAdapter() {
	    				@Override
	    				public void windowClosing(WindowEvent e) {
	    					new ServerGui();
	    					mainFrame.setVisible(false);
	    					mainFrame.dispose();
	    				}
    				});
    				

    		        JPanel content = new JPanel(new BorderLayout());
    		        
    		        port = new JPanel();
    		        port.setLayout(new BoxLayout(port, BoxLayout.Y_AXIS));
    		        port.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
    		        portLabel = new JLabel("Server Port");
    		        portLabel.setFont(new Font("Arial", Font.PLAIN, 30));
    		        portBox = new JTextField();
    		        portBox.setFont(new Font("Arial", Font.PLAIN, 32));
    		        port.add(portLabel);
    		        port.add(portBox);
    		        
    		        listing = new JPanel();
    		        listingLabel = new JLabel("Port Knocks");
    		        listingLabel.setFont(new Font("Arial", Font.PLAIN, 30));
    		        listing.add(listingLabel);
    		        listing.setLayout(new BoxLayout(listing, BoxLayout.Y_AXIS));
    		        GridLayout grid = new GridLayout(Integer.parseInt(sequenceNumber), 1);
    		        grid.setVgap(5);
    		        numbers.setLayout(grid);
    		        for(int i = 0; i < Integer.parseInt(sequenceNumber); i ++) {
    		            JTextField field = new JTextField();
    		            field.setFont(new Font("Arial", Font.PLAIN, 32));
    		            portSequence.add(field);
    		        	listing.add(field);
    		        }
    		        listing.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
    		        
    		        

    		        buttonPanel = new JPanel();
    		        JButton startButton = new JButton("Start Server");
    		        startButton.setPreferredSize(new Dimension(170, 70));
    		        startButton.setFont(new Font("Arial", Font.PLAIN, 20));
    		        buttonPanel.add(startButton);
    		        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

    		        content.add(port, BorderLayout.NORTH);
    		        content.add(listing, BorderLayout.CENTER);
    		        content.add(buttonPanel, BorderLayout.SOUTH);
    		        
    		        mainFrame.setContentPane(content);
    		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		        
    		        startButton.addActionListener(new ActionListener() {
    					@Override
    					public void actionPerformed(ActionEvent arg0) {
    						
    					   	try {
    					   		boolean accepted = true;
	    						String end = portBox.getText();
	    						if(!(Integer.parseInt(end) >= 0) || !(Integer.parseInt(end) < 65536) || end == "") accepted = false;
	    						ArrayList<Integer> knockSequence = new ArrayList<Integer>();
	    						for(int j = 0; j < portSequence.size(); j++) {
	    							String port = portSequence.get(j).getText();		    							
    								if(!(Integer.parseInt(port) >= 0) || !(Integer.parseInt(port) < 65536) || port == "") accepted = false;
									knockSequence.add(Integer.parseInt(port));
	    						}
	    						
	    						if(startButton.getText() == "Start Server" && accepted) {
									startButton.setText("Stop Server");
									Thread.sleep(10);
									int knockPort = Integer.parseInt(end);
							    	MyServer server = new MyServer(knockPort, knockSequence);
							    	server.runTCPDUmp();
								} else if (startButton.getText() == "Stop Server" && accepted) {
									int input = JOptionPane.showConfirmDialog(null, "Connections will be lost and iptables will remain appended!",
											"Shut down server?", JOptionPane.YES_NO_OPTION);
									if(input == 0) {
										startButton.setText("Start Server");
										new ServerGui();
										mainFrame.setVisible(false);
										mainFrame.dispose();
									}
									
								}
    					   	} catch (IOException | InterruptedException | NumberFormatException e) {
								e.printStackTrace();
							}
    					}    
    		        });    	        
    		        mainFrame.pack();
    		        mainFrame.setResizable(false);
    		        mainFrame.setLocationRelativeTo(null);
    		        mainFrame.setVisible(true);	
	    			}
	    	    });
        }
        numbers.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        content.add(knock, BorderLayout.NORTH);
        content.add(numbers, BorderLayout.SOUTH);
        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
   
    public static void main(String[] args) {
        new ServerGui();
    }

}