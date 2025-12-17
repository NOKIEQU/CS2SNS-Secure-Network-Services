import java.io.*;
import java.net.*;

public class ChatClient {
    public static void main(String[] args) {
        // 1. The server's address. "localhost" means "this same computer".
        String hostname = "localhost"; 
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