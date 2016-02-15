package main;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.*;
import java.io.IOException;

import javax.swing.*;
import java.net.*;

/**
 * GUI chat client runner.
 */
public class Client extends JFrame implements ActionListener{

    /**
     * Start a GUI chat client.
     */
	
	TextField msgSend;
	TextArea msgRec;
	
	JButton send;
	
	JPanel p1;
	
	Socket cSocket = null;
	
	DataOutputStream out;
	DataInputStream in;
	
	String sendMsg = "";
	
	public void chatFrame(){
		msgSend = new TextField(40); 
		msgRec = new TextArea();
		
		send = new JButton("Send");
		
		p1 = new JPanel();
		
		this.setSize(400, 500);
		this.setLocation(800, 100);
		
		this.setVisible(true);
		
		this.add(p1, BorderLayout.SOUTH);
		this.add(msgRec, BorderLayout.CENTER);
		p1.add(msgSend);
		p1.add(send);

		
		send.addActionListener(this);
		this.addWindowListener(new WindowAdapter(){
			
			@Override
			public void windowClosing(WindowEvent arg0){
				try {
					cSocket.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				System.exit(0);
			}
		});
		setVisible(true);
		startConnection();
		
		new Thread(new recieveMsg()).start();
	}
	
	public void startConnection(){
		try {
			cSocket = new Socket("127.0.0.1", 8908);
			out = new DataOutputStream(cSocket.getOutputStream());
			in = new DataInputStream (cSocket.getInputStream());
System.out.println("Connected!");			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void close(){
		try {
			in.close();
			out.close();
			cSocket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
	
    public static void main(String[] args) {
        // YOUR CODE HERE
        // It is not required (or recommended) to implement the client in
        // this runner class.
    	new Client().chatFrame();
    	
    }

	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		if (e.getSource()==send){
			sendMsg = msgSend.getText().trim();
			//msgRec.setText(msgRec.getText()+sendMsg+"\n");
			msgRec.setText(msgRec.getText() +"You say: "+ sendMsg + "\n");
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

	private class recieveMsg implements Runnable{


		public void run() {
			try {
				while (true){			
					String recMsg = in.readUTF();
					if (recMsg.equals(sendMsg)){
						msgRec.setText(msgRec.getText());
					}else{
						//msgRec.setText(msgRec.getText() + recMsg+"\n");
						msgRec.setText(msgRec.getText() +"Your friend says: "+ recMsg +"\n");
					}						
				}
			
			}	catch (IOException e) {
					//e.printStackTrace();
System.out.println("Your have closed cilent");
				}
				
			
		}
		
	}
}
