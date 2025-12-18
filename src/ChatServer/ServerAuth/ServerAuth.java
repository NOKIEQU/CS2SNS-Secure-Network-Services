import java.util.concurrent.ConcurrentHashMap;
import java.io.*;

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

    private boolean checkLoginCredentials(String username, String password) {
        // Returns true only if user exists AND password matches
        ConcurrentHashMap<String,User> users = db.getUsers();
        System.out.println(users.containsKey(username));
        return users.containsKey(username) && users.get(username).getPassword().equals(password);
    }
    private boolean checkIfUserLoggedin(String username){
        ConcurrentHashMap<String,LoggedinUser> users = db.getLoggedinUsers();
        return users.containsKey(username);
    }

    public LoggedinUser loginUser(String username, String password,PrintWriter writer){
        if(checkUserExists(username)){
            if(checkIfUserLoggedin(username))
                throw new IllegalArgumentException("User Already Loggedin");

            if(!checkLoginCredentials(username,password))
                throw new IllegalArgumentException("Invalid Username or Password");
            LoggedinUser user = db.addClient(username,writer);
            return user;
        }else {
            registerUser(username,password);
            return loginUser(username,password,writer);
        }
    }


    public User registerUser(String username, String password){

        User user = db.addUser(username,password);
        return user;

    }
    public void logoutUser(String username){
        db.removeClient(username);
    }
}