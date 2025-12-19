import javax.net.ssl.*;
import java.io.*;
import java.util.*;

public class ChatClient {
    
    public static void main(String[] args) {
	ChatClient client = new ChatClient();
	client.start();
    }
    
    public void start() {
	//connecting to server
	SSLSocket socket = establishServerConnection();

	// LISTENER THREAD
	// Handles incoming messages and server commands
	new Thread(() -> ChatClient.printServerMessage(socket)).start();
	
	// send message to server
	this.typeMessage(socket);
    }
    
    private static SSLSocket establishServerConnection() {
	//taking hostname & port from env if not present default to localst:49152
	String hostname = System.getenv().getOrDefault("HOST", "localhost");
	int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "49152"));
	
	try {
	    //SSL TrustStore
	    System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
	    System.setProperty("javax.net.ssl.trustStorePassword", "verystrongpassword123");
	    
	    //create ssl socket and try to establish connection with server
	    SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
	    SSLSocket socket = (SSLSocket) sslFactory.createSocket(hostname, port);
	    return socket;
	} catch (IOException e) {
	    System.out.println(e);
	    System.exit(1);
	}
	return null;
    }
    
    private static void printServerMessage(SSLSocket socket) {
	// Input from Server
	try {
	    //in is the messages server sends
	    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    String serverMsg;
	    //print while server sends something
	    while ((serverMsg = in.readLine()) != null) {
		System.out.println(serverMsg);
	    }
	} catch (IOException e) {
	    System.out.println("Disconnected from server.");
	    System.exit(0);
	}
	
    };
    
    private void typeMessage(SSLSocket socket) {
	// Input from User Keyboard
	try {
	    //take user input
	    Scanner scanner = new Scanner(System.in);
	    //for server output
	    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    //print after user hits enter and generates new line
	    while (scanner.hasNextLine()) {
		String input = scanner.nextLine();
		out.println(input);
		if ("/logout".equals(input))//exit the app when logout
		    System.exit(0);
	    }
	} catch (IOException e) {
	    System.out.println("Disconnected from server.");
	    System.exit(0);
	}
    };
}
