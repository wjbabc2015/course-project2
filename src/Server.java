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
	
	List<clientThread> clientList = new ArrayList<clientThread>();
		
    public static void main(String[] args) {
        // YOUR CODE HERE
        // It is not required (or recommended) to implement the server in
        // this runner class.
    	new Server().start();
    }
    
    public void start(){
		try {
			serverSocket = new ServerSocket(8908);
System.out.println("Server started!");
			while (true){
				cSocket = serverSocket.accept();
				
				clientThread c = new clientThread(cSocket);
System.out.print("A client connected!\n");

				new Thread(c).start();
				
				clientList.add(c);
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
    
    class clientThread implements Runnable{
    	
    	private Socket s;
    	private DataInputStream myIn = null;
    	private DataOutputStream myOut = null;
    	
    	
    	public clientThread(Socket s){
    		this.s = s;
    		try {
				myIn = new DataInputStream (s.getInputStream());
				myOut = new DataOutputStream(s.getOutputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
    	public void sendMsg(String msg)throws SocketException{
    		try {
				myOut.writeUTF(msg);
				//myOut.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
    	}
    	
		public void run() {
			clientThread c=null;
			try{				
			while (true){
				String msg = myIn.readUTF();
System.out.println(msg);		
				for (int i=0;i<clientList.size(); i++){
					c = clientList.get(i);
					c.sendMsg(msg);
				}
		}
	}catch (SocketException e){
		if (c!=null) clientList.remove(this);
	}catch (EOFException e){
		System.out.println("Client exited!");
	}catch (IOException e){
		e.printStackTrace();
	}finally{
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
