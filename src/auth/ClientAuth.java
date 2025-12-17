package auth;
import java.io.*;
import java.net.*;

public class ClientAuth {
    private String firstName;
    private String lastName;
    private String email;
    private String id;

    public ClientAuth(String firstName, String lastName, String email){
	this.firstName = firstName;
	this.lastName = lastName;
	this.email = email;
	this.id = "1";
    }
    public String getFirstName(){
	return this.firstName;
    };
    public String getLastName(){
	return this.lastName;
    };
    public String getEmail(){
	return this.email;
    };
    public String getId(){
	return this.id;
    };
}
