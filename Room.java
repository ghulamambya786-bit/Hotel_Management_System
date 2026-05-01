package hotel.management.model;

public class Room {
    private int id, capacity;
    private String roomNumber, roomType, status, description, floor;
    private double pricePerNight;

    public Room(){}
    public int getId(){return id;} public void setId(int id){this.id=id;}
    public String getRoomNumber(){return roomNumber;} public void setRoomNumber(String r){this.roomNumber=r;}
    public String getRoomType(){return roomType;} public void setRoomType(String t){this.roomType=t;}
    public double getPricePerNight(){return pricePerNight;} public void setPricePerNight(double p){this.pricePerNight=p;}
    public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
    public String getDescription(){return description;} public void setDescription(String d){this.description=d;}
    public String getFloor(){return floor;} public void setFloor(String f){this.floor=f;}
    public int getCapacity(){return capacity;} public void setCapacity(int c){this.capacity=c;}
    @Override public String toString(){return roomNumber+" - "+roomType+" (PKR "+pricePerNight+")";}
}
