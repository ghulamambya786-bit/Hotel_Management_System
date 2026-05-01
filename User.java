package hotel.management.model;

public class User {
    private int id;
    private String username, password, role, fullName, email;

    public User() {}
    public User(int id, String username, String role, String fullName, String email) {
        this.id=id; this.username=username; this.role=role; this.fullName=fullName; this.email=email;
    }
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public String getUsername(){return username;} public void setUsername(String u){this.username=u;}
    public String getPassword(){return password;} public void setPassword(String p){this.password=p;}
    public String getRole(){return role;} public void setRole(String r){this.role=r;}
    public String getFullName(){return fullName;} public void setFullName(String f){this.fullName=f;}
    public String getEmail(){return email;} public void setEmail(String e){this.email=e;}
    public boolean isAdmin(){return "admin".equalsIgnoreCase(role);}
    @Override public String toString(){return fullName;}
}
