import javax.net.ssl.*;
import java.io.*;
import java.util.Scanner;

public class ChatClient {

    public static void main(String[] args) {
        String hostname = "localhost";
        int port = 12345;

        // SSL TrustStore (Same as Level 2)
        System.setProperty("javax.net.ssl.trustStore", "keystore.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "verystrongpassword123");

        try {
            SSLSocketFactory sslFactory = (SSLSocketFactory) SSLSocketFactory.getDefault();
            SSLSocket socket = (SSLSocket) sslFactory.createSocket(hostname, port);
            
            // Output to Server
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            // Input from Server
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // Input from User Keyboard
            Scanner scanner = new Scanner(System.in);

            // --- LISTENER THREAD ---
            // Handles incoming messages and server commands
            new Thread(() -> {
                try {
                    String serverMsg;
                    while ((serverMsg = in.readLine()) != null) {
                        // Check for specific protocol commands
                        if (serverMsg.equals("SUBMIT_USERNAME")) {
                            System.out.print("Enter Username: ");
                        } 
                        else if (serverMsg.equals("SUBMIT_PASSWORD")) {
                            System.out.print("Enter Password: ");
                        } 
                        else if (serverMsg.equals("LOGIN_SUCCESS")) {
                            System.out.println("=== LOGIN SUCCESSFUL! YOU CAN CHAT NOW ===");
                        } 
                        else {
                            // Normal chat message
                            System.out.println(serverMsg);
                        }
                    }
                } catch (IOException e) {
                    System.out.println("Disconnected from server.");
                    System.exit(0);
                }
            }).start();

            // --- MAIN SENDER LOOP ---
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                out.println(input);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
