//This class holds information about all users and loggedIn users(session)
//loggedIn Users have their output stram 

interface LoggedInUser {
    name: String;
    output: PrintWriter;
}

public class ServerDb {
    // Track all registered users
    private Map<String, String> userDatabase = new ConcurrentHashMap<>();
    
    // Track connected users so nobody can log in twice at the same time
    public Map<String,LoggedInUser> connectedUsers = ConcurrentHashMap.newKeySet();
    

    public void addClient(PrintWriter writer) {
        allClientWriters.add(writer);
    }
    
    public void removeClient(PrintWriter writer, String username) {
        allClientWriters.remove(writer);
        if (username != null) {
            connectedUsers.remove(username);
            System.out.println(username + " has left.");
            broadcast("SERVER: " + username + " has left the chat.", null);
        }
    }
    
    public void broadcast(String message, PrintWriter excludeWriter) {
        for (PrintWriter writer : allClientWriters) {
            if (writer != excludeWriter) {
                writer.println(message);
            }
        }
    }
}
