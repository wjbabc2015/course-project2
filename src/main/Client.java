package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import javax.swing.*;
import java.net.*;

/**
 * GUI chat client runner.
 */
public class Client {

	/**
     * Start a GUI chat client.
     */
	public Client(){
		
	}
	
	public static void main(String[] args) {
        // YOUR CODE HERE
        // It is not required (or recommended) to implement the client in
        // this runner class.
    	loginFrame lf = new loginFrame();
    	lf.setVisible(true);
		lf.addWindowListener(new WindowAdapter(){
			
			@Override
			public void windowClosing(WindowEvent arg0) {
				System.exit(0);
			}
		});
    	
    }
}

/**
 * Create simple login frame 
 * @author Jeremy Wang
 */
class loginFrame extends JFrame implements ActionListener{
	String name = "";

	JPanel p;
	JLabel nickName;
	JTextField nickNameT;

	JButton login;

	/**
	 * Creator for loginFrame
	 * Create a login window to get the nickname, and pass it to chat frame
	 */
	public loginFrame(){
		
		p = new JPanel();
		nickName = new JLabel ("Nick Name: ");
		nickNameT = new JTextField(10);
	
		login = new JButton ("Login");
	
		p.add(nickName);
		p.add(nickNameT);
		p.add(login);
		this.add(p);
		this.setTitle("IM Start");
		this.setSize(210, 110);	
		this.setLocation(600, 100);
	
		login.addActionListener(this);
	}

	/**
	 * Monitor the action of button
	 * @param e action event, which reflects the action of login button
	 * Initialize the chat window with nickname, and close the login window
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==login){
		name = nickNameT.getText();
		this.dispose();//close the previous GUI when the new one opened
		chatFrame cf = new chatFrame (name);
		cf.setTitle(name + "'s chatting frame.");
		cf.setVisible(true);
		}
	}
}	

/**
 * The main frame for user to chat with others
 * @author Jeremy Wang
 */
class chatFrame extends JFrame{
	
	String name = "";
	
	TextField msgSend;
	TextArea msgRec;
	
	JButton send;
	
	JPanel p1;
	
	Socket cSocket = null;
	
	DataOutputStream out;
	DataInputStream in;
	
	String sendMsg = "";
	
	/**
	 * Creator for chat frame
	 * @param n string of words. requires not null
	 */
	public chatFrame(String n){
		name = n;
		
		msgSend = new TextField(40); 
		msgRec = new TextArea();
		
		send = new JButton("Send");
		
		p1 = new JPanel();//Create a panel for organizing the text field and button
		
		this.setSize(400, 500);
		this.setLocation(800, 100);
		
		this.setVisible(true);
		
		//Border layout for chat frame
		this.add(p1, BorderLayout.SOUTH);
		this.add(msgRec, BorderLayout.CENTER);
		p1.add(msgSend);
		p1.add(send);
		
		msgRec.setEditable(false);//Disable the ability of editing the text area

		
		send.addActionListener(new msgListener());//Create button action listener
		msgSend.addActionListener(new msgListener());//Create enter key action listener
		
		//Rewrite window action, add server connection and new thread start methods
		this.addWindowListener(new WindowAdapter(){
			
			//Rewrite the windowClosing method to add close method
			@Override
			public void windowClosing(WindowEvent arg0){
				/*try {
					cSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}*/
				close();
				System.exit(0);
			}
		});
		
		setVisible(true);
		startConnection();//The connection will be built with the open of this window
		
		new Thread(new recieveMsg()).start();//Each time a connection is built, start a new thread to hold the connection
	}
	
	/**
	 * Implement ActionListener for message sending method
	 * Display message in text area of chat window, put it in data output stream, 
	 * and clean the text field for input after message sending
	 **/
	private class msgListener implements ActionListener{
		
		//Rewrite the actionPerformed method to adjust the message sending function
		@Override
		public void actionPerformed(ActionEvent e) {
			String sendTemp = msgSend.getText().trim();
			sendMsg = name + " says: " + sendTemp;
			//msgRec.setText(msgRec.getText()+sendMsg+"\n");
			msgRec.setText(msgRec.getText() +"You say: "+ sendTemp + "\n");
			try {
				out.writeUTF(sendMsg);//Put data to output stream
				out.flush();//Refrest DataOutputStream to let server receive data
				//out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			msgSend.setText("");			
		}
		
	}
/*
	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==send){
			String sendTemp = msgSend.getText().trim();
			sendMsg = name + " says: " + sendTemp;
			//msgRec.setText(msgRec.getText()+sendMsg+"\n");
			msgRec.setText(msgRec.getText() +"You say: "+ sendTemp + "\n");
			try {
				out.writeUTF(sendMsg);
				out.flush();
				//out.close();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
			msgSend.setText("");
		}
		
	}
*/	
	

	/**
	 * Connect client to the server
	 * Set up IP and port of client, build up input and output stream
	 * Return message for connection status
	 **/
	public void startConnection(){
		try {
			msgRec.setText("Connecting server...\n");
			cSocket = new Socket("127.0.0.1", 8908);
			msgRec.setText(msgRec.getText() +"Connected server, setting up I/O stream...\n");
			out = new DataOutputStream(cSocket.getOutputStream());
			in = new DataInputStream (cSocket.getInputStream());
			msgRec.setText(msgRec.getText() +"Congrats, you can chat now...\n");
//System.out.println("Connected!");			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Disconnect client to server
	 **/
	public void close(){
		try {
			in.close();
			out.close();
			cSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
	/**
	 * Implement runnable class for receiving message
	 * @author Jeremy Wang
	 * Display the message recieved from server on the text area
	 **/
	private class recieveMsg implements Runnable{


		public void run() {
			try {
				while (true){			
					String recMsg = in.readUTF();
					
					int pos = recMsg.indexOf(':');
					String tagS = recMsg.substring(0, pos);
					//Disable the message sent from client itself
					if (tagS.equals(name + " says")){
						msgRec.setText(msgRec.getText());
					}else{
						//msgRec.setText(msgRec.getText() + recMsg+"\n");
						msgRec.setText(msgRec.getText() + recMsg +"\n");
					}						
				}
			
			}	catch (IOException e) {
					//e.printStackTrace();
System.out.println("Your have closed cilent");
				}
				
			
		}
		
	}
}
