package src;

import javax.swing.*;
import java.awt.event.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;

public class ClientGui extends JFrame {

	private JFrame frame, mainFrame;
    private JPanel knock, numbers, listing, port, buttonPanel, address;
    private JLabel knockLabel, portLabel, listingLabel, addressLabel;
    private ArrayList<JTextField> portSequence = new ArrayList<JTextField>();
    private String sequenceNumber;
    private JTextField portBox, addressBox;
    private static final String serverPubKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAzfpsmXHMJvbJZf2eAeesQXzbABl9cpMFT+vkrdoEsRaK8GaprjOsZC32aM4HZinW/S2jmXNeKBhnfPGsNgT3dPRTMaLH9VAh2GsGVms98PLvVTPBmZFloo5nTacPqQWgm8qkPoZfUOyuEqodsfkf0CQK5I84n0GsorEmNNbDlLeA66OcgPto/2LBMqZAPTwGPanHDEpcEB11IkcsdHHmEBLvuyjSvQnORpJJc33SU/L0XyR/MZndwsgUAHxj8SvJ4kfgIpdBcBZiiHEZVjFOdre9YhQ4dTbQDBWODXUsfXNpbWBECihQbC8adwdtVQA2oLDorHpna10V3DzicQrHIQIDAQAB";
    private static final String clientPrivKey = "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQCr2dXGetuwg5Tmr23pUbiGIr9EU7g1y4Vwjgz2RHEDcR071e7kf6Sh7/fUwOPrqxRDnSxrJSn+7kQOaBcSgG3nBoblTiOnulTIsVKzqDGYVrYzWqe6sW/Lgu6e6LcK7eudT/oyIvsg/nscIdrvoB6Lxs7Anbnl0DJK2a28SqJly2zZVlST6xg3UnAYERXJepOnY0fTJt1jUalhpHgRmesHz7n7SGWzRd1Yadb+WjfY86XKs6PZ3uwkSPykTP9YRSMo42JHsGBHXczQb2tSFCqVHJFD2oUjvx5DRosme2dvxkwt9cHEzJqz7aHucLeIxzkra0rteJxtnA2qj1GB33jDAgMBAAECggEABdWadWecZ8y/1GkmUSKOIjexyK8S9PiY6i5YcE/4ikZf2LU1rBpNS5YrEtUNp+WiBX2jkWusllQeiWKtTdqAYLSPBd/2Q+6GjF3A0gqmzGY9P1cXlLYLdpxWX5zFAiD7u9aIRwV7Ay7AnT7lAPetsc8pG87/DbdYzcPAK0FZbB1CdcT1N94g4o3QI/3cybDRe+Z57M6DSAYe+1ogRikAqD7QLD2pnLU+ypVspR84Lq+LbLh+2D+hnZ+hiW52s5khzR24A5OOHUNzq7eAfl+tOhLkyNExpEqB79DnUkBPd8ttT2nd0EY3tIPSPfmj8qSFO7aypccz06jPe7Dj0q4RCQKBgQD7auN9gML1xZKdUEWv4CirDh9qlTudvOxnRAIOYdJtpPqB2GIA+6sCFNfWBsgGjmhYxsm+irgcQCxXkRcmExwd8fVfy37d9jwpZ4f/abhmxPx6rJHeK8jGXfPqFgnXWC2lUByla8cND6egss/1D6Oo4WZ+FQVIff9cDnXgHuQ8PQKBgQCu+7CC/SQi03d8+JP3ZNIPopbW/wxCdvIzkHK1IWxFcLNYOxSptVH37r/FEzM8dml2LMSNb4rtM3jmw9JPT4jQ81T4U4NZfisUmwxyBRpYCLgYAUDGjaIYk+Gg0PmXcZUSsU0PrOThNAh6S6JhGmTQlB9zn0jq59R6yQsd58TY/wKBgDIOPeymzioQg/g+GyHBB1fHIAogXBYznv2QVLh4UWTzC8z+P8TiVlG7xJ0gDIMBp1TFfzUoeS9mt21Xvbwe4eI0Yh4IbeHTPHch3bnEWqpbXckuwnvxS6/y1LUuXhc5vxzCrnFg2+iFzWH5N94alwLwnW9M0Bh2vXieiGluvRIhAoGAFeY1/w7DWQByMdfCXPHnQEGu8xVUaXUNdtqbIIQgUsh6CY7LVTn2GjttELMIdIa1SC7uIm1VS75nYSocxgREMTJi7fk1tRuPNLL66cItu9rLf2WYv8C2CrFnSYMd2ZBDgeViqZWPx4eFkBirJ8/v0hLXpNbIf22oL/29QG7jOe0CgYEAlPogHkE+5Q7OfJwabc56PMkNSm2ruFQ7WqLxie0jUXnIDYxr6TUFnrwpAmrwNmsZ+l8NOSjgHwJhb9taJBNGbtXWieNx0TcjeVRoXssV5BKksT2v4PLbjS3Vl/gBiIX3iszLTQBj7QJLNWT1VPKGMA2Buw57gtBZKyC8lQ4LVyc=";
    
    public ClientGui() {
        
    	// FRAME 1 :- contains buttons for knock sequence size from 3-6
    	// opening JFrame to click button for knock sequence size
    	frame = new JFrame("Client");
    	frame.setPreferredSize(new Dimension(400, 300));
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
        
        // JPanel holding the 4 buttons on 2x2 GridLayout
        numbers = new JPanel();
        numbers.setLayout(new GridLayout(2, 2, 30, 30));
        // for loop for buttons from 3-6
        for (int i = 3; i < 7; i++) {
        	// create buttons and add to 'button' JPanel
        	JButton button = new JButton("" + i);
        	button.setFont(new Font("Arial", Font.PLAIN, 24));
        	button.setPreferredSize(new Dimension(100, 80));
        	numbers.add(button);
        	// actions listener for each button for click event
        	button.addActionListener(new ActionListener() {
    			@Override
    			public void actionPerformed(ActionEvent arg0) {
    				// dispose of opening JFrame containing 4 buttons
    				frame.setVisible(false);
    				frame.dispose();
    				// get text on button (knock sequence size)
    				sequenceNumber = button.getText();
    				
    				// FRAME 2 :- contains input fields for server port, ipaddress and knock sequence
    				// main frame containing knock packet information of the server
    				mainFrame = new JFrame("Client");
    				mainFrame.setLayout(new BorderLayout());
    				// click event listener on the main frame close button
    				mainFrame.addWindowListener(new WindowAdapter() {
    					@Override
    					public void windowClosing(WindowEvent e) {
    						//create a new opening client (i.e. show opening frame)
    						new ClientGui();
    						// dispose of main frame
    						mainFrame.setVisible(false);
    						mainFrame.dispose();
    					}
    				});
    				
    				// JPanel to hold main frame content
    		        JPanel content = new JPanel(new BorderLayout());
    		        
    		        // JPanel to contain information north of the panel
    		        // add gridlayout of 2 rows (hold server address and port)
    		        GridLayout gridLayout = new GridLayout(2,1);
    		        JPanel north = new JPanel(gridLayout);
    		        
    		        // JPanel constructed for server IPAddress
    		        address = new JPanel();
    		        address.setLayout(new BoxLayout(address, BoxLayout.Y_AXIS));
    		        address.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
    		        addressLabel = new JLabel("Server IP Address");
    		        addressBox = new JTextField();    
    		        addressLabel.setFont(new Font("Arial", Font.PLAIN, 30));
    		        addressBox.setFont(new Font("Arial", Font.PLAIN, 32));
    		        address.add(addressLabel);
    		        address.add(addressBox);
    		        
    		        // JPanel constructed for server port
    		        port = new JPanel();
    		        port.setLayout(new BoxLayout(port, BoxLayout.Y_AXIS));
    		        port.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); 
    		        portLabel = new JLabel("Server Port");
    		        portBox = new JTextField(); 
    		        portLabel.setFont(new Font("Arial", Font.PLAIN, 30));
    		        portBox.setFont(new Font("Arial", Font.PLAIN, 32));
    		        port.add(portLabel);
    		        port.add(portBox);
    		        
    		        // add both JPanels to the north JPanel containing the gridlayout
    		        north.add(address);
    		        north.add(port);
    		        
    		        // JPanel constructed for port knocks
    		        listing = new JPanel();
    		        listingLabel = new JLabel("Port Knocks");
    		        listingLabel.setFont(new Font("Arial", Font.PLAIN, 30));
    		        listing.add(listingLabel);
    		        listing.setLayout(new BoxLayout(listing, BoxLayout.Y_AXIS)); 
    		        GridLayout grid = new GridLayout(Integer.parseInt(sequenceNumber), 1);
    		        grid.setVgap(5);
    		        numbers.setLayout(grid);
    		        // loop to create port knock input field corresponting to the number on the button previously clicked
    		        for(int i = 0; i < Integer.parseInt(sequenceNumber); i++) {
    		        	JTextField field = new JTextField();
    		        	field.setFont(new Font("Arial", Font.PLAIN, 32));
    		        	portSequence.add(field);
    		        	listing.add(field);
    		        }
    		        listing.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
    		        
    		        // JPanel to contain button to send knock sequence to server
    		        buttonPanel = new JPanel();
    		        JButton startButton = new JButton("Send Knock");
    		        startButton.setPreferredSize(new Dimension(170, 70));
    		        startButton.setFont(new Font("Arial", Font.PLAIN, 20));
    		        buttonPanel.add(startButton);
    		        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 5, 5, 5));

    		        // add all 3 JPanels to main JPanels
    		        // add main JPanel to main JFrame
    		        content.add(north, BorderLayout.NORTH);
    		        content.add(listing, BorderLayout.CENTER);
    		        content.add(buttonPanel, BorderLayout.SOUTH);
    		        mainFrame.setContentPane(content);
    		        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					
    		        // add action listener to above button to send knock sequence
    		        startButton.addActionListener(new ActionListener() {
						@Override
    					public void actionPerformed(ActionEvent arg0) {
							boolean accepted = true;
							
							// retrieve text of server port & check if its valid
							String endPort  = portBox.getText();
							if(!isInteger(endPort, 10)) {
								accepted = false;
							} else if(!(Integer.parseInt(endPort) >= 0) || !(Integer.parseInt(endPort) < 65536)) accepted = false;

							// retrieve text of server ipaddress & check if its valid
							String addr = addressBox.getText();
							InetAddress addressIP = null;
							// imported library to check for valid IPV6/IPV4 addresses
							InetAddressValidator validAddr = new InetAddressValidator();
							if (!validAddr.isValid(addr)) {
								accepted = false;
							} else {
								try {
									addressIP = InetAddress.getByName(addr);
								} catch (UnknownHostException e) {
									e.printStackTrace();
								}
							}
							
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
    						// if all fields are valid send port knock sequence
    						if(accepted) {
								new MyClient(addressIP, knockSequence, serverPubKey, clientPrivKey);
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
    
    // main method to create client GUI
    public static void main(String[] args) {
    	new ClientGui();
    }
}