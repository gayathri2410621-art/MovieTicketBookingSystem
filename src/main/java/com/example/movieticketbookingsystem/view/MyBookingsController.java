package com.example.movieticketbookingsystem.view;

import com.example.movieticketbookingsystem.model.Booking;
import com.example.movieticketbookingsystem.model.Seat;
import com.example.movieticketbookingsystem.model.Ticket;
import com.example.movieticketbookingsystem.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;

public class MyBookingsController {

    @FXML
    private VBox bookingsContainer;

    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMM d, yyyy hh:mm a");

    @FXML
    public void initialize() {
        loadBookings();
    }

    private void loadBookings() {
        new Thread(() -> {
            EntityManager em = null;
            try {
                em = HibernateUtil.getEntityManager();

                // Only fetch CONFIRMED (active) bookings
                List<Booking> bookings = em.createQuery(
                                "SELECT DISTINCT b FROM Booking b WHERE b.status = 'CONFIRMED' ORDER BY b.bookingTime DESC",
                                Booking.class)
                        .getResultList();

                Platform.runLater(() -> {
                    bookingsContainer.getChildren().clear();

                    if (bookings.isEmpty()) {
                        Label noBookings = new Label("No active bookings found");
                        noBookings.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
                        bookingsContainer.getChildren().add(noBookings);
                    } else {
                        for (Booking booking : bookings) {
                            bookingsContainer.getChildren().add(createBookingCard(booking));
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Failed to load bookings: " + e.getMessage());
                    alert.showAndWait();
                });
            } finally {
                // CRITICAL FIX: Always close EntityManager
                if (em != null && em.isOpen()) {
                    em.close();
                }
            }
        }).start();
    }

    private VBox createBookingCard(Booking booking) {
        VBox card = new VBox(10);
        card.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10;");
        card.setMaxWidth(800);

        // Header
        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        Label bookingId = new Label("Booking #" + booking.getId());
        bookingId.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label bookingTime = new Label(booking.getBookingTime().format(DATETIME_FORMATTER));
        bookingTime.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        // Status badge
        Label statusLabel = new Label(booking.getStatus());
        if (booking.isCancelled()) {
            statusLabel.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 15; -fx-font-weight: bold;");
        } else {
            statusLabel.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 5 15; -fx-background-radius: 15; -fx-font-weight: bold;");
        }

        HBox.setHgrow(bookingTime, Priority.ALWAYS);
        bookingTime.setMaxWidth(Double.MAX_VALUE);
        bookingTime.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().addAll(bookingId, bookingTime, statusLabel);

        // Fetch tickets
        EntityManager em = null;
        try {
            em = HibernateUtil.getEntityManager();

            List<Ticket> tickets = em.createQuery(
                            "SELECT t FROM Ticket t " +
                                    "JOIN FETCH t.show s " +
                                    "JOIN FETCH s.movie " +
                                    "JOIN FETCH s.screen sc " +
                                    "JOIN FETCH sc.theater " +
                                    "JOIN FETCH t.seat " +
                                    "WHERE t.booking.id = :bookingId",
                            Ticket.class)
                    .setParameter("bookingId", booking.getId())
                    .getResultList();

            if (!tickets.isEmpty()) {
                Ticket firstTicket = tickets.get(0);

                Label movieTitle = new Label(firstTicket.getShow().getMovie().getTitle());
                movieTitle.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

                Label showInfo = new Label(
                        firstTicket.getShow().getShowTime().format(DateTimeFormatter.ofPattern("MMM d, yyyy hh:mm a")) +
                                " | " + firstTicket.getShow().getScreen().getTheater().getName() +
                                " - Screen " + firstTicket.getShow().getScreen().getScreenNumber()
                );
                showInfo.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");

                // Build seats string
                StringBuilder seatsStr = new StringBuilder("Seats: ");
                for (int i = 0; i < tickets.size(); i++) {
                    Ticket ticket = tickets.get(i);
                    seatsStr.append(ticket.getSeat().getSeatRow()).append(ticket.getSeat().getSeatNumber());
                    if (i < tickets.size() - 1) {
                        seatsStr.append(", ");
                    }
                }
                Label seatsLabel = new Label(seatsStr.toString());
                seatsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e;");

                card.getChildren().addAll(header, new Separator(), movieTitle, showInfo, seatsLabel);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // CRITICAL FIX: Always close EntityManager
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        // Footer
        HBox footer = new HBox(20);
        footer.setAlignment(Pos.CENTER_LEFT);

        Label totalAmount = new Label("Total: ₹" + String.format("%.2f", booking.getTotalAmount()));
        totalAmount.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        HBox.setHgrow(totalAmount, Priority.ALWAYS);

        // Cancel button
        if (booking.isConfirmed()) {
            Button cancelButton = new Button("Cancel Booking");
            cancelButton.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-font-size: 14; -fx-padding: 10 20; -fx-background-radius: 5; -fx-cursor: hand;");
            cancelButton.setOnAction(event -> handleCancelBooking(booking));
            footer.getChildren().addAll(totalAmount, cancelButton);
        } else {
            Label cancelledText = new Label("(Cancelled)");
            cancelledText.setStyle("-fx-font-size: 14px; -fx-text-fill: #e74c3c; -fx-font-style: italic;");
            footer.getChildren().addAll(totalAmount, cancelledText);
        }

        card.getChildren().add(footer);
        return card;
    }

    private void handleCancelBooking(Booking booking) {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Cancel Booking");
        confirmAlert.setHeaderText("Are you sure you want to cancel this booking?");
        confirmAlert.setContentText(
                "Booking ID: " + booking.getId() + "\n" +
                        "Amount: ₹" + booking.getTotalAmount() + "\n\n" +
                        "This action cannot be undone."
        );

        Optional<ButtonType> result = confirmAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            new Thread(() -> {
                EntityManager em = null;
                EntityTransaction transaction = null;

                try {
                    em = HibernateUtil.getEntityManager();
                    transaction = em.getTransaction();
                    transaction.begin();

                    Booking bookingToCancel = em.find(Booking.class, booking.getId());

                    if (bookingToCancel != null && bookingToCancel.isConfirmed()) {
                        bookingToCancel.setStatus("CANCELLED");
                        em.merge(bookingToCancel);

                        List<Ticket> tickets = em.createQuery(
                                        "SELECT t FROM Ticket t WHERE t.booking.id = :bookingId",
                                        Ticket.class)
                                .setParameter("bookingId", booking.getId())
                                .getResultList();

                        for (Ticket ticket : tickets) {
                            Seat seat = em.find(Seat.class, ticket.getSeat().getId());
                            if (seat != null) {
                                seat.setAvailable(true);
                                em.merge(seat);
                            }
                        }

                        transaction.commit();

                        Platform.runLater(() -> {
                            Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                            successAlert.setTitle("Success");
                            successAlert.setHeaderText(null);
                            successAlert.setContentText("Booking cancelled successfully!");
                            successAlert.showAndWait();

                            loadBookings();
                        });
                    } else {
                        transaction.rollback();
                        Platform.runLater(() -> showError("Booking cannot be cancelled."));
                    }

                } catch (Exception e) {
                    if (transaction != null && transaction.isActive()) {
                        transaction.rollback();
                    }
                    e.printStackTrace();
                    Platform.runLater(() -> showError("Error cancelling booking: " + e.getMessage()));
                } finally {
                    // CRITICAL FIX: Always close EntityManager
                    if (em != null && em.isOpen()) {
                        em.close();
                    }
                }
            }).start();
        }
    }

    @FXML
    private void handleRefresh() {
        loadBookings();
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) bookingsContainer.getScene().getWindow();
        stage.close();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
