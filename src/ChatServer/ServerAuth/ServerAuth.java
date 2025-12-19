package ChatServer.ServerAuth;
import java.util.concurrent.ConcurrentHashMap;
import java.io.*;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.SecureRandom;
import java.util.Base64;

import ChatServer.ServerDb.ServerDb;
import ChatServer.User.User;
import ChatServer.LoggedinUser.LoggedinUser;


public class ServerAuth {
    ServerDb db;

    public ServerAuth(ServerDb db){
	this.db = db;
    }

    private boolean checkUserExists(String username) {
	    //return true if username is in the db
        ConcurrentHashMap<String,User> users = db.getUsers();
        return users.containsKey(username);
    }

    private boolean checkLoginCredentials(String username, char[] password) {
        // Returns true only if user exists AND password matches
        ConcurrentHashMap<String,User> users = db.getUsers();

	if(!users.containsKey(username)) return false;
        try {
	    String storedPassword = users.get(username).getPassword();
            String[] parts = storedPassword.split(":");

            int iterations = Integer.parseInt(parts[0]);
            byte[] salt = Base64.getDecoder().decode(parts[1]);
            byte[] hash = Base64.getDecoder().decode(parts[2]);

            PBEKeySpec spec = new PBEKeySpec(password,salt,iterations,hash.length * 8);

            SecretKeyFactory skf =
                    SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");

            byte[] testHash = skf.generateSecret(spec).getEncoded();

	    int diff = hash.length ^ testHash.length;
	    for (int i = 0; i < hash.length && i < testHash.length; i++) {
		diff |= hash[i] ^ testHash[i];
	    }
	    return diff == 0;
        } catch (Exception e) {
            return false;
        }
    }
    private boolean checkIfUserLoggedin(String username){
        ConcurrentHashMap<String,LoggedinUser> users = db.getLoggedinUsers();
        return users.containsKey(username);
    }

    public LoggedinUser loginUser(String username, String password,PrintWriter writer){
        if(checkUserExists(username)){
            if(checkIfUserLoggedin(username))
                throw new IllegalArgumentException("User Already Loggedin");

            if(!checkLoginCredentials(username,password.toCharArray()))
                throw new IllegalArgumentException("Invalid Username or Password");
            LoggedinUser user = db.addClient(username,writer);
            return user;
        }else {
            registerUser(username,password.toCharArray());
            return loginUser(username,password,writer);
        }
    }


    public User registerUser(String username, char[] password){
	final int SALT_LENGTH = 16;
	final int ITERATIONS = 65536;
	final int KEY_LENGTH = 256;
	
        try {
	    byte[] salt = new byte[SALT_LENGTH];
	    new SecureRandom().nextBytes(salt);
	    
            PBEKeySpec spec = new PBEKeySpec(password,salt,ITERATIONS,KEY_LENGTH);
	    
            SecretKeyFactory skf =
		SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
	    
            byte[] hash = skf.generateSecret(spec).getEncoded();
	    
            // Store: iterations:salt:hash
            String storedPassword =  ITERATIONS + ":" +
		Base64.getEncoder().encodeToString(salt) + ":" +
		Base64.getEncoder().encodeToString(hash);
	    
	    User user = db.addUser(username,storedPassword);
	    return user;
        } catch (Exception e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }
    public void logoutUser(String username){
        db.removeClient(username);
    }
}
