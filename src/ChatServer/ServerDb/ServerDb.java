package ChatServer.ServerDb;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

// CORRECT IMPORTS: Must match the folder name "ChatServer"
import ChatServer.User.User;
import ChatServer.LoggedinUser.LoggedinUser;

public class ServerDb {
    // Track all registered users
    private ConcurrentHashMap<String, User> users;
    
    // Track connected users so nobody can log in twice at the same time
    public ConcurrentHashMap<String, LoggedinUser> connectedUsers;

    public ServerDb() {
        this.users = new ConcurrentHashMap<>();
        this.connectedUsers = new ConcurrentHashMap<>();
    }

    public ConcurrentHashMap<String, LoggedinUser> getLoggedinUsers(){
        return this.connectedUsers;
    }

    public LoggedinUser addClient(String username, PrintWriter writer) {
        LoggedinUser user = new LoggedinUser(username, writer);
        connectedUsers.put(username, user);
        return user;
    }
    
    public void removeClient(String username) {
        connectedUsers.remove(username);
    }

    public ConcurrentHashMap<String, User> getUsers(){
        return users;
    }

    public User addUser(String username, String password){
        User user = new User(username, password);
        users.put(username, user);
        return user;
    }

    public void removeUser(String username){
        users.remove(username);
    }
}