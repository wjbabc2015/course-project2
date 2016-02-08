

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.ServerSocket;

/**
 * Chat server runner.
 */
public class Server extends ServerSocket {

    /**
     * Start a chat server.
     */
	
	private static final int severPort = 8907;
	public Server() throws IOException {
		super(severPort);
		try{
			while(true){
				Socket socket = accept();
				new startNewServerThread(socket);
			}
		}catch (Exception e){
			
		}finally {
			close();
		}
	}
	
	class startNewServerThread extends Thread{
		
		private Socket client;
		private BufferedReader bufferedReader;
		private PrintWriter printWriter;
		
	    public startNewServerThread(Socket socket)throws IOException {
			// TODO Auto-generated method stub
			client = socket;
			
			bufferedReader = new BufferedReader (new InputStreamReader(client.getInputStream()));
			printWriter = new PrintWriter (client.getOutputStream(), true);
			
			System.out.println("Client(" + getName() +") come in...");
			
			start();
		}
	    
	    public void run(){
	    	try{
	    		String line = bufferedReader.readLine();
	    		
	    		while(!line.equals("exit")){
	    			printWriter.println("continue, Client(" + getName() +")!");
	    			
	    			System.out.println("Client(" + getName() +") say: " + line);
	    			line = bufferedReader.readLine();
	    		}
	    		printWriter.println("Client(" + getName() +") exit!");
	    	
	    		System.out.println("Client(" + getName() +") exit!");
                printWriter.close();
                bufferedReader.close();
                client.close();               
	    }catch (Exception e){
	    	
	    }
	}
	}
	public static void main(String[] args)throws IOException {
        // YOUR CODE HERE
        // It is not required (or recommended) to implement the server in
        // this runner class.
    	new Server();
    }
}
