import javax.net.ssl.*;
import java.io.*;
import java.util.*;

public class ChatClient {
    private String jwt;

    public static void main(String[] args) {
	ChatClient client = new ChatClient();
	client.start();
    }
    
    public void start(){
	SSLSocket socket = establishServerConnection();

	// --- LISTENER THREAD ---
	// Handles incoming messages and server commands
	new Thread(() -> this.printServerMessage(socket)).start();
	
	//Send Auth Info To server
	//	this.handleAuth(socket); //this will block the thread

	// --- SENDER ---
	this.typeMessage(socket);
	
    }

    private static SSLSocket establishServerConnection(){
	String hostname = "localhost";
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "49152"));

        //establish connection with server
        try {
	    // SSL TrustStore
	    System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
	    System.setProperty("javax.net.ssl.trustStorePassword", "verystrongpassword123");
	    
	    SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
	    SSLSocket socket = (SSLSocket) sslFactory.createSocket(hostname, port);
	    return socket;
        } catch (IOException e) {
        System.out.println(e.getMessage());
	    System.exit(1);
        }
	return null;
    }

    private static void handleAuth(SSLSocket socket){
	try{
	Scanner scanner = new Scanner(System.in);

	// Output to Server
	PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	//server response
	BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));

	String line;
	while ((line = in.readLine()) != null && !line.startsWith("LOGIN_SUCCESS")) {
	    out.println(scanner.nextLine());
	}
	} catch (IOException e) {
	    System.exit(0);
	}
    }
    
    private static void printServerMessage(SSLSocket socket){
	// Input from Server

	try {
	    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    String serverMsg;
	    while ((serverMsg = in.readLine()) != null) {
		System.out.println(serverMsg);
	    }
	} catch (IOException e) {
	    System.out.println("Disconnected from server.");
	    System.exit(0);
	}
	
    };
    private void typeMessage(SSLSocket socket){
	try{
	    // Input from User Keyboard
	    Scanner scanner = new Scanner(System.in);
	    // Output to Server
	    PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
	    
	    while (scanner.hasNextLine()) {
		String input = scanner.nextLine();
		out.println(input);
		if ("/Logout".equals(input))
		    System.exit(0);
	    }
	} catch (IOException e) {
	    System.out.println("Disconnected from server.");
	    System.exit(0);
	}
	
    };
}
