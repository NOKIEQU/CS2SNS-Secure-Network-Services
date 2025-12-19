package ChatServer;

import java.io.PrintWriter;
import java.io.StringWriter;
import ChatServer.ServerDb.ServerDb;
import ChatServer.ServerAuth.ServerAuth;
import ChatServer.LoggedinUser.LoggedinUser;

public class ServerTests {

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("RUNNING AUTOMATED TESTS FOR CS2SNS PROJECT");
        System.out.println("==========================================\n");

        testRegistrationAndLogin();
        testInvalidPassword();
        testDuplicateLoginPrevention();
        
        System.out.println("\n==========================================");
        System.out.println("ALL TESTS COMPLETED");
        System.out.println("==========================================");
    }

    public static void testRegistrationAndLogin() {
        System.out.print("[TEST 1] Registration & Valid Login: ");
        try {
            // Setup
            ServerDb db = new ServerDb();
            ServerAuth auth = new ServerAuth(db);
            PrintWriter dummyWriter = new PrintWriter(new StringWriter());

            // 1. Register
            auth.registerUser("Alice", "secret123".toCharArray());

            // 2. Login
            LoggedinUser user = auth.loginUser("Alice", "secret123", dummyWriter);
            
            if (user != null && user.getName().equals("Alice")) {
                System.out.println("PASSED");
            } else {
                System.out.println("FAILED (User object mismatch)");
            }
        } catch (Exception e) {
            System.out.println("FAILED (" + e.getMessage() + ")");
            e.printStackTrace();
        }
    }

    public static void testInvalidPassword() {
        System.out.print("[TEST 2] Security Control (Wrong Password): ");
        try {
            ServerDb db = new ServerDb();
            ServerAuth auth = new ServerAuth(db);
            PrintWriter dummyWriter = new PrintWriter(new StringWriter());

            // Register
            auth.registerUser("Bob", "securePass".toCharArray());

            // Attempt Hack
            try {
                auth.loginUser("Bob", "wrongPass", dummyWriter);
                System.out.println("FAILED (Allowed invalid password)");
            } catch (IllegalArgumentException e) {
                if (e.getMessage().equals("Invalid Username or Password")) {
                    System.out.println("PASSED (Correctly rejected)");
                } else {
                    System.out.println("FAILED (Wrong error message: " + e.getMessage() + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("FAILED (System Error)");
        }
    }

    public static void testDuplicateLoginPrevention() {
        System.out.print("[TEST 3] Security Control (Anti-Spoofing): ");
        try {
            ServerDb db = new ServerDb();
            ServerAuth auth = new ServerAuth(db);
            PrintWriter dummyWriter = new PrintWriter(new StringWriter());

            // Register & Login First Instance
            auth.registerUser("Charlie", "myPass".toCharArray());
            auth.loginUser("Charlie", "myPass", dummyWriter);

            // Attempt Second Login (Impersonation attempt)
            try {
                auth.loginUser("Charlie", "myPass", dummyWriter);
                System.out.println("FAILED (Allowed duplicate login)");
            } catch (IllegalArgumentException e) {
                if (e.getMessage().equals("User Already Loggedin")) {
                    System.out.println("PASSED (Blocked duplicate session)");
                } else {
                    System.out.println("FAILED (Wrong error: " + e.getMessage() + ")");
                }
            }
        } catch (Exception e) {
            System.out.println("FAILED");
        }
    }
}