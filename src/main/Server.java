package main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Chat server runner.
 */
public class Server {

    /**
     * Start a chat server.
     */
	
	ServerSocket serverSocket = null;
	Socket cSocket = null;
	
	DataInputStream in;
	DataOutputStream out;
	
	List<clientThread> clientList = new ArrayList<clientThread>();//Array List for storing the client sockets
	List<String> messageList = new ArrayList<String>();//Array list for storing offline message
	
	boolean tag = false;
		
    public static void main(String[] args) {
        // YOUR CODE HERE
        // It is not required (or recommended) to implement the server in
        // this runner class.
    	new Server().start();
    }
    
    /**
     * Start server
     * Prepare the server for client connecting
     * Monitor client connecting, and start a new thread for each client connection
     * Add new thread in client list 
     **/
    public void start(){
		try {
			serverSocket = new ServerSocket(8908);//Define server socket with port number
//System.out.println("Server started!");
			while (true){ 
				//Monitor client request, start client thread to issue
				cSocket = serverSocket.accept();
				
				clientThread c = new clientThread(cSocket);
//System.out.print("A client connected!\n");
				

				new Thread(c).start();//Start the client thread
				
				clientList.add(c);//Add the new client thread in array list
				
				//When a client connected, check if there are more than two clients
				//if so, set tag to get the offline message
				if (clientList.size()>1){
					tag = true;
				}
				
				}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				serverSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}			
		}
	}
    
    /**
     * Client thread class to solve multiple client
     * @author Jeremy Wang
     */
    class clientThread implements Runnable{
    	
    	private Socket s;
    	private DataInputStream myIn = null;
    	private DataOutputStream myOut = null;
    	
    	/**
    	 * Initialize I/O stream
    	 * @param s socket for client
    	 * return a new thread
    	 */
    	public clientThread(Socket s){
    		this.s = s; 		
    		try {
				myIn = new DataInputStream (s.getInputStream());
				myOut = new DataOutputStream(s.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	/**
    	 * Send message method 
    	 * @param msg string of words. requires not null
    	 * @throws SocketException
    	 * Allow server to send message to every client
    	 */
    	public void sendMsg(String msg)throws SocketException{
    		try {
				myOut.writeUTF(msg);
				//myOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	/*
    	 * Client thread start, and get all past message from the message list
    	 * Put message in message list, and send to all clients which are connected to server
    	 * @see java.lang.Runnable#run()
    	 */
		public void run() {
			clientThread c=null;
			try{
				//When a new client connected, send all the message in the list to the client.
				while (tag){
					//Send the string to all the client within the array list
					for (int i=0;i<messageList.size(); i++){
						String msgT = messageList.get(i);					
							c = clientList.get(clientList.size() - 1);
							c.sendMsg(msgT);
						//messageList.remove(i);
					}
					tag = false;//after message sent, close the function
				}
				
				while (true){
					String msg = myIn.readUTF();//Get data stream from client
//System.out.println(msg);
				
					messageList.add(msg);
				
						for (int j =0; j<clientList.size(); j++){
							c = clientList.get(j);
							c.sendMsg(msg);
						}
					}
				
			
			//Exception for taking care the array list, socket, and I/O stream
			}catch (SocketException e){
				if (c!=null) clientList.remove(this);
			}catch (EOFException e){
				System.out.println("Client exited!");
			}catch (IOException e){
				e.printStackTrace();
			}finally{
				//When exception is caught, close all I/O stream and socket
				try {
					if(myIn!=null) myIn.close();
					if(myOut!=null)myOut.close();
					if(s!=null) s.close();
				} catch (IOException e1) {
					e1.printStackTrace();
				}	
			}		
   }
}
}