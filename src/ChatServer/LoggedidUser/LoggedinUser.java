package Server;
import java.io.*;

class LoggedinUser {
    private String name;
    private PrintWriter output;

    public LoggedinUser(String name, PrintWriter output){
	this.name = name;
	this.output = output;
    }

    public PrintWriter getOutput() {
        return this.output;
    }
    public String getName(){
        return this.name;
    }
}
