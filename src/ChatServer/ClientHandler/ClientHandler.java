package ChatServer.ClientHandler;

import java.io.*;
import java.net.Socket;
import ChatServer.ServerAuth.ServerAuth;
import ChatServer.LoggedinUser.LoggedinUser;
import ChatServer.Server; // Import the renamed Server class

public class ClientHandler implements Runnable {
    private Socket socket;
    private PrintWriter out;
    private BufferedReader in;
    private LoggedinUser user = null; 
    private ServerAuth authHandler;

    public ClientHandler(Socket socket, ServerAuth auth) {
        this.socket = socket;
        this.authHandler = auth;
    }

    @Override
    public void run() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        
            // Call handle login until user successfully logs in
            while ((this.user = this.handleUserLogin(out, this.authHandler)) == null) {};

            System.out.println(user.getName() + " logged in successfully.");
            // UPDATED: Call Server.broadcast instead of ChatServer.broadcast
            Server.broadcast("SERVER: " + user.getName() + " has joined!", out);
        
            // CHAT LOOP
            String message;
            while ((message = in.readLine()) != null) {
                if ("/Logout".equals(message)) {
                    try {socket.close();} catch (IOException e) {}
                    continue;
                }
                this.broadcastMessage(user.getName(), message);
            }
        } catch (IOException e) {
            System.out.println("Connection Error: " + e.getMessage());
        } finally {
            if(this.user != null) {
                this.authHandler.logoutUser(this.user.getName());
                // UPDATED: Call Server.broadcast
                Server.broadcast("SERVER: " + this.user.getName() + " has left the chat.", out);
            }
            try {socket.close();} catch (IOException e) {}
        }
    }

    private LoggedinUser handleUserLogin(PrintWriter out, ServerAuth auth){
        try {
            out.println("Enter User Name");
            String username = in.readLine();
            out.println("Enter Password");
            String password = in.readLine();

            if (username == null || password == null) return null; 
            try {
                LoggedinUser user = auth.loginUser(username, password, out);
                return user;
            } catch (IllegalArgumentException e){
                out.println(e.getMessage());
                return null;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void broadcastMessage(String username, String message){
        String formattedMessage = "[" + username + "]: " + message;
        System.out.println(formattedMessage);
        // UPDATED: Call Server.broadcast
        Server.broadcast(formattedMessage, out);
    }
}