package Server;
import javax.net.ssl.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ChatServer {
    private static ServerDb db = new ServerDb();
    private static ServerAuth auth = new ServerAuth(db);

    public static void main(String[] args) {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "49152"));

        // SSL Setup
        System.setProperty("javax.net.ssl.keyStore", "keystore.jks");
        System.setProperty("javax.net.ssl.keyStorePassword", "verystrongpassword123");

        try {
            SSLServerSocketFactory sslFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
            SSLServerSocket serverSocket = (SSLServerSocket) sslFactory.createServerSocket(port);

            System.out.println("SECURE Chat Server started on port " + port);

            while (true) {
                SSLSocket clientSocket = (SSLSocket) serverSocket.accept();
                new Thread(new ClientHandler(clientSocket,auth)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void broadcast(String message, PrintWriter excludeWriter) {
        ConcurrentHashMap<String,LoggedinUser> users = db.getLoggedinUsers();
        Collection<LoggedinUser> usersCollection = users.values();
        for (LoggedinUser user : usersCollection) {
            if(user.getOutput() != excludeWriter)
                user.getOutput().println(message);
        }
    }
}
