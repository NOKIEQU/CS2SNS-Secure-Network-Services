
public class ServerAuth(ServerDb db) {
    
    public static boolean checkUserExists(String username) {
	//return true if username is in the db
	return userDatabase.containsKey(username);
    }
    public static boolean checkLogin(String username, String password) {
        // Returns true only if user exists AND password matches
        return userDatabase.containsKey(username) && userDatabase.get(username).equals(password);
    }
    
    public static void registerUser(String username, String password){
	userDatabase.put(username,password);
    }
}
