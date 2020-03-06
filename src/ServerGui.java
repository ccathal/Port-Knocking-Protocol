package src;

import javax.swing.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class ServerGui extends JFrame {

    private JFrame frame, mainFrame;
    private JPanel knock, numbers, listing, port, buttonPanel;
    private JLabel knockLabel, portLabel, listingLabel;
    private ArrayList<JTextField> portSequence = new ArrayList<JTextField>();
    private String sequenceNumber;
    private JTextField portBox;

    public ServerGui() {
        
    	// FRAME 1 :- contains buttons for knock sequence size form 3-6
    	// opening JFrame to click button for knock sequence size
    	frame = new JFrame("Server");
    	frame.setPreferredSize(new Dimension(425, 310));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // JPanel to hold content of opening JFrame
        JPanel content = new JPanel(new BorderLayout());
        
        // JPanel to contain 4 buttons with knock sequence size
        knock = new JPanel();
        knock.setLayout(new BoxLayout(knock, BoxLayout.Y_AXIS));
        knock.setBorder(BorderFactory.createEmptyBorder(10, 10, 5, 10));
        knockLabel = new JLabel("Choose Knock Sequence Length");
        knockLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        knock.add(knockLabel);
        
        // JPanel holding 4 buttons on 2x2 gridlayout
        numbers = new JPanel();
        numbers.setLayout(new GridLayout(2, 2, 30, 30));
        // for loop for buttons from 3-6
        for (int i = 3; i < 7; i++) {
        	// create buttons and add to 'button' JPanel
        	JButton button = new JButton("" + i);
        	button.setPreferredSize(new Dimension(80, 90));
        	button.setFont(new Font("Arial", Font.PLAIN, 23));
        	numbers.add(button);
        	// action listener for each button for click event
        	button.addActionListener(new ActionListener() {
    			@Override
    			public void actionPerformed(ActionEvent arg0) {
    				// dispose of opening JFrame containing 4 buttons
    				frame.setVisible(false);
					frame.dispose();
					//get text on button (knock sequence size)
    				sequenceNumber = button.getText();
    				
    				// FRAME 2 :- containing input fields for server port, ipaddress and knock sequence
    				// main frame containing knock packet information of the server
    				mainFrame = new JFrame("Server");
    				mainFrame.setLayout(new BorderLayout());
    				// click event listener on the main frame close button
    				mainFrame.addWindowListener(new WindowAdapter() {
	    				@Override
	    				public void windowClosing(WindowEvent e) {
	    					// create a new opening client (i.e. show opening frame)
	    					new ServerGui();
	    					// dispose of the main frame
	    					mainFrame.setVisible(false);
	    					mainFrame.dispose();
	    				}
    				});
    				
    				// JPanel to hold main frame content
    		        JPanel content = new JPanel(new BorderLayout());
    		        
    		        // JPanel constructed for server IPAddress
    		        port = new JPanel();
    		        port.setLayout(new BoxLayout(port, BoxLayout.Y_AXIS));
    		        port.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
    		        portLabel = new JLabel("Server Port");
    		        portLabel.setFont(new Font("Arial", Font.PLAIN, 30));
    		        portBox = new JTextField();
    		        portBox.setFont(new Font("Arial", Font.PLAIN, 32));
    		        port.add(portLabel);
    		        port.add(portBox);
    		        
    		        // JPanel constructed for port knocks
    		        listing = new JPanel();
    		        listingLabel = new JLabel("Port Knocks");
    		        listingLabel.setFont(new Font("Arial", Font.PLAIN, 30));
    		        listing.add(listingLabel);
    		        listing.setLayout(new BoxLayout(listing, BoxLayout.Y_AXIS));
    		        GridLayout grid = new GridLayout(Integer.parseInt(sequenceNumber), 1);
    		        grid.setVgap(5);
    		        numbers.setLayout(grid);
    		        // loop to create port knock input fields corresponding to the number on the button previously clicked
    		        for(int i = 0; i < Integer.parseInt(sequenceNumber); i ++) {
    		            JTextField field = new JTextField();
    		            field.setFont(new Font("Arial", Font.PLAIN, 32));
    		            portSequence.add(field);
    		        	listing.add(field);
    		        }
    		        listing.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
    		        
    		        // JPanel to contain button to send knock sequence to server
    		        buttonPanel = new JPanel();
    		        JButton startButton = new JButton("Start Server");
    		        startButton.setPreferredSize(new Dimension(170, 70));
    		        startButton.setFont(new Font("Arial", Font.PLAIN, 20));
    		        buttonPanel.add(startButton);
    		        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

    		        // add all 3 JPanels to main JPanel
    		        // add main JPanel to main JFrame
    		        content.add(port, BorderLayout.NORTH);
    		        content.add(listing, BorderLayout.CENTER);
    		        content.add(buttonPanel, BorderLayout.SOUTH);
    		        mainFrame.setContentPane(content);
    		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		        
    		        // add action listener to above button to send knock sequence
    		        startButton.addActionListener(new ActionListener() {
    					@Override
    					public void actionPerformed(ActionEvent arg0) {
    						try {
    					   		boolean accepted = true;
    					   		
    					   		// retrieve text of server port & check if its valid
	    						String endPort = portBox.getText();
	    						if(!isInteger(endPort, 10)) {
	    							accepted = false;
		    					} else if(!(Integer.parseInt(endPort) >= 0) || !(Integer.parseInt(endPort) < 65536)) accepted = false;
	    						
	    						// loop through knock ports text & check if its valid
	    						ArrayList<Integer> knockSequence = new ArrayList<Integer>();
	    						for(int j = 0; j < portSequence.size(); j++) {
	    							String port = portSequence.get(j).getText();
		    						if(!isInteger(port, 10)) {
		    							accepted = false;
			    					} else if(!(Integer.parseInt(port) >= 0) || !(Integer.parseInt(port) < 65536)) {
			    						accepted = false;
			    					} else knockSequence.add(Integer.parseInt(port));
	    						}
	    						
	    						// if server has not started yet and all input fields valid, start server
	    						if(startButton.getText() == "Start Server" && accepted) {
									startButton.setText("Stop Server");
									int knockPort = Integer.parseInt(endPort);
							    	MyServer server = new MyServer(knockPort, knockSequence);
							    	server.runTCPDUmp();
							    // else server is already running and want to turn it off
							    // user must confirm first
								} else if (startButton.getText() == "Stop Server") {
									int input = JOptionPane.showConfirmDialog(null, "Connections will be lost and iptables will remain appended!",
											"Shut down server?", JOptionPane.YES_NO_OPTION);
									// if yes shut down server
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
    		        // settings for main JFrame
    		        mainFrame.pack();
    		        mainFrame.setResizable(false);
    		        mainFrame.setLocationRelativeTo(null);
    		        mainFrame.setVisible(true);	
	    			}
	    	    });
        }
        numbers.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // add 2 JPanels to main opening JPanel
        // add opening JPanel to opening JFrame
        content.add(knock, BorderLayout.NORTH);
        content.add(numbers, BorderLayout.SOUTH);
        frame.setContentPane(content);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        // settings for opening JFrame
        frame.pack();
        frame.setResizable(false);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
    
    // method to check if an input is an integer or not
    public static boolean isInteger(String s, int radix) {
    	if(s.isEmpty()) return false;
    	for(int i = 0; i < s.length(); i++) {
    		if(i == 0 && s.charAt(i) == '-') {
    			if(s.length() == 1) return false;
    			else continue;
    		}
    		if(Character.digit(s.charAt(i), radix) < 0) return false;
    	}
    	return true;
    }
   
    // main method to create server GUI
    public static void main(String[] args) {
        new ServerGui();
    }

}