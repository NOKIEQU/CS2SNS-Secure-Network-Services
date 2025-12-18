
class User {
    private  String name;
    private String password;

    public User(String name, String password) {
        this.name = name;
        this.password = password;//here would be nice idea to encrypt but na
    }
    public String getName(){
        return this.name;
    }
    public String getPassword(){
        return this.password;
    }
}