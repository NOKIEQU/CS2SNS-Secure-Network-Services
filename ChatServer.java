import javax.net.ssl.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    // 1. DATABASE of valid users (Username -> Password)
    // In a real app, this would be in a database file.
    private static Map<String, String> userDatabase = new ConcurrentHashMap<>();
    
    // 2. Track connected users so nobody can log in twice at the same time
    public static Set<String> connectedUsers = ConcurrentHashMap.newKeySet();
    
    // 3. Output streams for broadcasting
    private static Set<PrintWriter> allClientWriters = ConcurrentHashMap.newKeySet();

    public static void main(String[] args) {
        int port = 12345;

        // --- PRE-REGISTER SOME USERS FOR TESTING ---
        userDatabase.put("alice", "password123");
        userDatabase.put("bob", "securepass");
        userDatabase.put("admin", "admin");

        // SSL Setup (Same as Level 2)
        System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "verystrongpassword123");

        try {
            SSLServerSocketFactory sslFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) sslFactory.createServerSocket(port);
            
            System.out.println("SECURE Chat Server (Level 3) started on port " + port);
            System.out.println("Allowed users: alice, bob, admin");

            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                new Thread(new ClientHandler(clientSocket)).start();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // --- AUTHENTICATION HELPERS ---
    
    public static boolean checkLogin(String username, String password) {
        // Returns true only if user exists AND password matches
        return userDatabase.containsKey(username) && userDatabase.get(username).equals(password);
    }

    // --- BROADCAST HELPERS ---

    public static void addClient(PrintWriter writer) {
        allClientWriters.add(writer);
    }

    public static void removeClient(PrintWriter writer, String username) {
        allClientWriters.remove(writer);
        if (username != null) {
            connectedUsers.remove(username);
            System.out.println(username + " has left.");
            broadcast("SERVER: " + username + " has left the chat.", null);
        }
    }

    public static void broadcast(String message, PrintWriter excludeWriter) {
        for (PrintWriter writer : allClientWriters) {
            if (writer != excludeWriter) {
                writer.println(message);
            }
        }
    }
}