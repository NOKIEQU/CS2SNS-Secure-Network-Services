import java.io.*;
import java.net.*;
import auth.ClientAuth;

public class ChatClient {
    private ClientAuth auth;

    public static void main(String[] args) {
        BufferedReader consoleReader =
                new BufferedReader(new InputStreamReader(System.in));

        try {
            System.out.print("Enter Your First Name: ");
            String firstName = consoleReader.readLine();
            System.out.print("Enter Your Last Name: ");
            String lastName = consoleReader.readLine();
            System.out.print("Enter Your Email: ");
	    String email = consoleReader.readLine();
	    ClientAuth auth = new ClientAuth(firstName,lastName,email);
	    this.auth = auth;
        } catch (IOException e) {
            e.printStackTrace();
        }
	System.out.println(this.auth.getFirstName());


        // 1. The server's address. "localhost" means "this same computer".
        String hostname = "192.168.1.224"; 
        int port = 12345;

        try {
            System.out.println("Attempting to connect to server...");

            // 2. Attempt to create a socket connection to the server
            Socket socket = new Socket(hostname, port);

            // 3. If no error is thrown, we are connected!
            System.out.println("Successfully connected to the Chat Server!");

        } catch (UnknownHostException e) {
            System.out.println("Server not found: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("I/O Error: " + e.getMessage());
        }
    }
}
