================================================================
   HOTEL MANAGEMENT SYSTEM - Setup Guide (Step by Step)
================================================================

DEFAULT LOGIN:
  Admin : admin    / admin123
  User  : user     / user123
  Manager: manager / manager123

================================================================
STEP 1 — MySQL Database Setup
================================================================
1. MySQL Workbench ya phpMyAdmin kholein
2. Is file ko kholein aur run karein:
      database/hotel_db.sql
3. Iske baad "hotel_db" database ban jayega
4. Sample rooms, customers, bookings bhi insert ho jayenge

================================================================
STEP 2 — MySQL Connector JAR Download
================================================================
1. Neeche diye link se JAR download karein:
   https://dev.mysql.com/downloads/connector/j/
   (Platform Independent > ZIP Archive select karein)

2. ZIP extract karein
3. "mysql-connector-java-X.X.X.jar" file dhundein
4. Is JAR file ko project ke "lib" folder mein copy karein:
      HotelManagementSystem/lib/mysql-connector-java.jar

================================================================
STEP 3 — Password Set Karein
================================================================
1. Is file kholein:
   src/hotel/management/db/DBConnection.java

2. Line dhundein:
   private static final String PASSWORD = "";

3. Wahan apna MySQL password likhein, jaise:
   private static final String PASSWORD = "mypassword123";

4. Agar password nahi lagaya MySQL mein toh khali rehne dein: ""

================================================================
STEP 4 — NetBeans mein Open Karein
================================================================
1. NetBeans IDE kholein
2. File → Open Project click karein
3. "HotelManagementSystem" folder select karein
4. "Open Project" click karein
5. Project left panel mein nazar aayega

================================================================
STEP 5 — MySQL Connector Library Add Karein (NetBeans)
================================================================
1. Projects panel mein project par Right-Click karein
2. "Properties" select karein
3. Left mein "Libraries" click karein
4. "Add JAR/Folder" button dabayein
5. lib/mysql-connector-java.jar select karein
6. OK click karein

================================================================
STEP 6 — Run Karein
================================================================
1. Green "Run Project" button (F6) dabayein
2. Login screen ayegi
3. Login karein:
   - Admin ke liye: admin / admin123
   - User ke liye:  user  / user123

================================================================
FEATURES
================================================================

ADMIN DASHBOARD:
  - Dashboard  : Stats, Today CheckIn/Out, Quick Access
  - Rooms      : Add/Edit/Delete Rooms
  - Customers  : Add/Edit/Delete Customers
  - Bookings   : New Booking, Update Status, Cancel
  - Check In   : Process Guest Check-In
  - Check Out  : Process Guest Check-Out
  - Billing    : Revenue tracking
  - Reports    : Booking & Room reports
  - Settings   : Hotel settings
  - Users      : Add/Edit/Delete system users

USER VIEW (Read Only):
  - Dashboard  : Stats overview
  - View Rooms
  - View Customers
  - View Bookings
  - Cannot add/edit/delete anything

================================================================
PROJECT STRUCTURE
================================================================
HotelManagementSystem/
├── src/hotel/management/
│   ├── Main.java                  ← Entry point
│   ├── db/
│   │   └── DBConnection.java      ← MySQL connection
│   ├── model/
│   │   ├── User.java
│   │   ├── Room.java
│   │   ├── Customer.java
│   │   └── Booking.java
│   ├── dao/
│   │   ├── UserDAO.java
│   │   ├── RoomDAO.java
│   │   ├── CustomerDAO.java
│   │   └── BookingDAO.java
│   └── ui/
│       ├── LoginFrame.java        ← Login screen
│       ├── Theme.java             ← Colors & styles
│       ├── AdminMainFrame.java    ← Admin dashboard frame
│       ├── DashboardPanel.java    ← Dashboard page
│       ├── RoomPanel.java         ← Rooms CRUD
│       ├── CustomerPanel.java     ← Customers CRUD
│       ├── BookingPanel.java      ← Bookings CRUD
│       ├── CheckInOutPanel.java   ← Check In/Out
│       ├── BillingPanel.java      ← Billing
│       ├── ReportsPanel.java      ← Reports
│       ├── SettingsPanel.java     ← Settings
│       ├── UserMgmtPanel.java     ← User management
│       └── UserMainFrame.java     ← User view only
├── database/
│   └── hotel_db.sql               ← Run this in MySQL
├── lib/
│   └── (mysql-connector-java.jar) ← Add manually
└── nbproject/
    ├── project.xml
    ├── project.properties
    └── build-impl.xml

================================================================
REQUIREMENTS
================================================================
  - Java JDK 8 or higher
  - NetBeans IDE 8+ / 12+ / 17+
  - MySQL Server 5.7+ or 8.0+
  - mysql-connector-java.jar

================================================================
