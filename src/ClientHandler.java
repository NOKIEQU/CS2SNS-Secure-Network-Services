import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private String clientName = null; // Will store the authenticated name

    public ClientHandler(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);

            out.println("WELCOME to the Secure Chat!");
            out.println("Please login to continue.");

            // --- PHASE 1: AUTHENTICATION LOOP ---
            while (true) {
                out.println("SUBMIT_USERNAME");
                String username = in.readLine();
                
                out.println("SUBMIT_PASSWORD");
                String password = in.readLine();

                if (username == null || password == null) return; // Client quit

                // 1. Check if user is already online
                if (ChatServer.connectedUsers.contains(username)) {
                    out.println("ERROR: User already logged in.");
                    continue;
                }

                // 2. Check credentials against database
                if (ChatServer.checkLogin(username, password)) {
                    out.println("LOGIN_SUCCESS");
                    clientName = username;
                    ChatServer.connectedUsers.add(clientName); // Mark as online
                    ChatServer.addClient(out);
                    System.out.println(clientName + " logged in successfully.");
                    
                    // Announce to room
                    ChatServer.broadcast("SERVER: " + clientName + " has joined!", out);
                    break; // EXIT LOGIN LOOP -> ENTER CHAT LOOP
                } else {
                    out.println("ERROR: Invalid Username or Password.");
                }
            }

            // --- PHASE 2: SECURE CHAT LOOP ---
            String message;
            while ((message = in.readLine()) != null) {
                // Formatting: Add the name to the front of the message
                // This identifies the user to everyone else
                String formattedMessage = "[" + clientName + "]: " + message;
                
                System.out.println("Broadcasting: " + formattedMessage);
                ChatServer.broadcast(formattedMessage, out);
            }

        } catch (IOException e) {
            System.out.println("Connection Error: " + e.getMessage());
        } finally {
            ChatServer.removeClient(out, clientName);
            try { socket.close(); } catch (IOException e) {}
        }
    }
}