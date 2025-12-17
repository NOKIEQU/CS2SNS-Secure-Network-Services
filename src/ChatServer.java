import java.io.*;
import java.net.*;

public class ChatServer {
    public static void main(String[] args) {
        // 1. Define the port number. Both client and server must agree on this.
        int port = 12345; 

        try {
            // 2. Create the ServerSocket. This listens for incoming connections.
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Server started! Waiting for a client to connect...");

            while (true) {
            // 3. The accept() method BLOCKS (pauses) the program until a client connects.
  
              try {
              Socket clientSocket = serverSocket.accept(); 
              System.out.println("A client has connected!");

              } catch (IOException e) {
                  System.out.println("Error accepting client connection: " + e.getMessage());
              }

            }

            // 4. If we reach this line, a connection was successful!
            
            // (Later, we will pass this 'clientSocket' to a new thread here)

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}