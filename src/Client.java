

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * GUI chat client runner.
 */
public class Client {
	private static final String clientIP = "127.0.0.1";
	private static final int clientPort = 8907;
	


    /**
     * Start a GUI chat client.
     */
    public static void main(String[] args) {
        // YOUR CODE HERE
        // It is not required (or recommended) to implement the client in
        // this runner class.
    	
    	PrintWriter printWriter = null;
    	BufferedReader sysBuff = null;
    	BufferedReader bufferedReader = null;
    	
    	try{

    		Socket socket = new Socket(clientIP, clientPort);
    	
    		printWriter = new PrintWriter(socket.getOutputStream(),true);
    		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    		
    		String result = "";
    		while(result.indexOf("exit") == -1){
        		System.out.print("What will you want to say: ");
        		
    			sysBuff = new BufferedReader(new InputStreamReader(System.in));
        		
        		printWriter.println(sysBuff.readLine());
        		printWriter.flush();
        		
        		result = bufferedReader.readLine();
        		System.out.println("Server say: " + result);
    		}
    		  		
    		
    		printWriter.close();
    		bufferedReader.close();
    		socket.close();   		 		   		
    	}catch (Exception e){
    		System.out.println("Exception:" + e);
    	}
    }
}
