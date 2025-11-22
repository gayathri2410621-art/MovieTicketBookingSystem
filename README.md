Movie Ticket Booking System
A JavaFX-based desktop application for browsing movies, selecting shows, choosing seats in real-time, and generating booking tickets—powered by Hibernate ORM and Oracle Database.
Features
Movie Management
Display all available movies with posters
View movie details (title, genre, duration)
Filter movies by category (optional)
🕒 Show Management
View showtimes for each movie
Display screen & date information
Fetch available shows dynamically
💺 Real-Time Seat Selection
Interactive seat layout generated with JavaFX GridPane
Available / Booked / Selected seat indicators
Prevents duplicate seat booking using DB constraints
🎟 Booking & Ticket Generation
Confirm booking with selected seats
Auto-generate ticket entries for each seat
View booking history
🛢 Database & ORM
Hibernate ORM handling CRUD operations
Oracle Database XE used for secure storage
Properly normalized schema (1NF → 3NF)
🎨 User-Friendly UI
Built using JavaFX (FXML + CSS)
Clean navigation
Responsive layout
🏛 System Architecture
Presentation Layer: JavaFX (FXML Views + Controllers)
Business Layer: Controllers & Service Logic
Persistence Layer: Hibernate ORM
Database Layer: Oracle Database XE
Entities include:
Movie, Show, Screen, Seat, Booking, Ticket
🗄 Technologies Used
Technology	Purpose
JavaFX	GUI, FXML screens, controllers
Hibernate	ORM, mapping models to DB tables
Oracle Database XE	Data storage
Java 17+	Project language
Maven/Gradle	Dependency management
Git & GitHub	Version control
📁 Project Structure
src/
 └── main/
      ├── java/
      │    ├── controllers/
      │    ├── dao/
      │    ├── model/
      │    ├── util/
      │    └── application/
      ├── resources/
      │    ├── fxml/
      │    ├── css/
      │    └── images/
      └── persistence.xml

🗃 Database Schema (Simplified)
Movie
movie_id, title, genre, duration, poster
Show
show_id, movie_id, screen_id, date, time
Screen
screen_id, theater_name, total_seats
Seat
seat_id, screen_id, seat_number, status
Booking
booking_id, show_id, user_id, booking_time
Ticket
ticket_id, booking_id, seat_id

▶ Running the Project
1. Clone repo
git clone https://github.com/gayathri2410621-art/MovieTicketBookingSystem.git
2. Configure database
Install Oracle Database XE
Create schema
Run table creation scripts
Update JDBC URL, username, password in persistence.xml
3. Run from IntelliJ
Load Maven/Gradle project
Run MainApplication.java
🌟 Future Enhancements
Online payment integration
Multi-user login
Dynamic pricing
Admin dashboard for scheduling shows

Email/SMS ticket confirmation
