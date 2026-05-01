package hotel.management.model;

public class Customer {
    private int id;
    private String fullName, cnic, phone, email, address, city;

    public Customer(){}
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public String getFullName(){return fullName;} public void setFullName(String f){this.fullName=f;}
    public String getCnic(){return cnic;} public void setCnic(String c){this.cnic=c;}
    public String getPhone(){return phone;} public void setPhone(String p){this.phone=p;}
    public String getEmail(){return email;} public void setEmail(String e){this.email=e;}
    public String getAddress(){return address;} public void setAddress(String a){this.address=a;}
    public String getCity(){return city;} public void setCity(String c){this.city=c;}
    @Override public String toString(){return fullName+" ("+cnic+")";}
}
