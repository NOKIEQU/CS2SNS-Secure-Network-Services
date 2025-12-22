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
	    //reading usre input
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	    //send him message
            out = new PrintWriter(socket.getOutputStream(), true);
        
            // Call handle login until user successfully logs in
            while ((this.user = this.handleUserLogin(out, this.authHandler)) == null) {};

	    //outputing to 
            System.out.println(user.getName() + " logged in successfully.");
            Server.broadcast("SERVER: " + user.getName() + " has joined!",user.getOutput());
        
            // CHAT LOOP
            String message;
	    //print while we have connection with user
            while ((message = in.readLine()) != null) {
		//do not broadcast message if its empty
		if (message.isEmpty()) continue;
		//special commands
                if ("/logout".equals(message)) {
                    try {socket.close();} catch (IOException e) {}
                    continue;
                }
                if ("/help".equals(message)) {
		    this.printHelpMessage(out);
		    continue;
		}
		if ("/clear".equals(message)) {
		    out.print("");
		    out.print("\033[2J\033[H");
		    out.flush();
		    out.println("! Chat Cleared");
		    continue;
		}
		if (!message.isEmpty() && message.charAt(0) == '/') {
		    out.println("Could not find the command");
		}
		//broadcast message to the whole groupchat
                this.broadcastMessage(user.getName(), message);
            }
        } catch (IOException e) {
            System.out.println("Server Connection Error");
        } finally {
	    //close socket, remove uesr broadcast this to chatafter he logout 
            if(this.user != null) {
                this.authHandler.logoutUser(this.user.getName());
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
		out.println("ju sukcesfuli ar logd in as " + user.getName());
		this.clearScreen(out);
		this.printHelpMessage(out);
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

    private void printHelpMessage(PrintWriter out){
	out.println("------------------------------");
	out.println("=== Welcome To Secure Chat ===");
	out.println("------------------------------");
	out.println("");
	out.println("To send message type your message in the chat");
	out.println("All the messages that start with '/' are considered system commands");
	out.println("To Logout type /logout in the chat");
	out.println("To clear the screen type /clear in the chat");
	out.println("To see this message again type /help");
    }

    private void clearScreen(PrintWriter out){
	out.print("\033[2J\033[H");
	out.flush();
    }
}
