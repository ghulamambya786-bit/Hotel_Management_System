package hotel.management.model;

import java.util.Date;

public class Booking {
    private int id, customerId, roomId;
    private String bookingCode, customerName, roomNumber, roomType;
    private Date checkIn, checkOut;
    private String checkInTime, checkOutTime, status, notes;
    private double totalAmount;

    public Booking(){}
    public int getId(){return id;} public void setId(int i){this.id=i;}
    public int getCustomerId(){return customerId;} public void setCustomerId(int c){this.customerId=c;}
    public int getRoomId(){return roomId;} public void setRoomId(int r){this.roomId=r;}
    public String getBookingCode(){return bookingCode;} public void setBookingCode(String b){this.bookingCode=b;}
    public String getCustomerName(){return customerName;} public void setCustomerName(String c){this.customerName=c;}
    public String getRoomNumber(){return roomNumber;} public void setRoomNumber(String r){this.roomNumber=r;}
    public String getRoomType(){return roomType;} public void setRoomType(String t){this.roomType=t;}
    public Date getCheckIn(){return checkIn;} public void setCheckIn(Date d){this.checkIn=d;}
    public Date getCheckOut(){return checkOut;} public void setCheckOut(Date d){this.checkOut=d;}
    public String getCheckInTime(){return checkInTime;} public void setCheckInTime(String t){this.checkInTime=t;}
    public String getCheckOutTime(){return checkOutTime;} public void setCheckOutTime(String t){this.checkOutTime=t;}
    public String getStatus(){return status;} public void setStatus(String s){this.status=s;}
    public String getNotes(){return notes;} public void setNotes(String n){this.notes=n;}
    public double getTotalAmount(){return totalAmount;} public void setTotalAmount(double t){this.totalAmount=t;}
}
