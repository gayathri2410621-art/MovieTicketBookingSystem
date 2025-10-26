package com.example.movieticketbookingsystem.view;

import com.example.movieticketbookingsystem.MainController;
import com.example.movieticketbookingsystem.dao.BookingDaoImpl;
import com.example.movieticketbookingsystem.dao.SeatDao;
import com.example.movieticketbookingsystem.dao.SeatDaoImpl;
import com.example.movieticketbookingsystem.dao.TicketDaoImpl;
import com.example.movieticketbookingsystem.model.Booking;
import com.example.movieticketbookingsystem.model.Seat;
import com.example.movieticketbookingsystem.model.Show;
import com.example.movieticketbookingsystem.model.Ticket;
import com.example.movieticketbookingsystem.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityTransaction;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class SeatSelectionController {

    @FXML private Label movieTitleLabel;
    @FXML private Label showTimeLabel;
    @FXML private Label screenInfoLabel;
    @FXML private GridPane seatGridPane;
    @FXML private FlowPane selectedSeatsFlowPane;
    @FXML private Label totalPriceLabel;

    private Show selectedShow;
    private MainController mainController;
    private SeatDao seatDao = new SeatDaoImpl();
    private BookingDaoImpl bookingDao = new BookingDaoImpl();
    private TicketDaoImpl ticketDao = new TicketDaoImpl();

    private Map<String, Seat> allSeatsMap = new HashMap<>();
    private List<Seat> selectedSeats = new ArrayList<>();
    private List<Long> bookedSeatIdsForThisShow = new ArrayList<>();

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a, EEEE MMM d");

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setShow(Show show) {
        this.selectedShow = show;
        updateShowInfo();
        loadSeats();
    }

    private void updateShowInfo() {
        if (selectedShow != null) {
            movieTitleLabel.setText(selectedShow.getMovie().getTitle());
            showTimeLabel.setText(selectedShow.getShowTime().format(DATE_TIME_FORMATTER));
            screenInfoLabel.setText(String.format("Screen %d at %s",
                    selectedShow.getScreen().getScreenNumber(),
                    selectedShow.getScreen().getTheater().getName()));
            updateTotalPrice();
        }
    }

    private void loadSeats() {
        if (selectedShow == null) return;

        new Thread(() -> {
            EntityManager em = null;
            try {
                em = HibernateUtil.getEntityManager();

                // Get all seats for this screen
                List<Seat> allSeatsInScreen = em.createQuery(
                                "SELECT s FROM Seat s WHERE s.screen = :screen ORDER BY s.seatRow, s.seatNumber",
                                Seat.class)
                        .setParameter("screen", selectedShow.getScreen())
                        .getResultList();

                // Get booked seat IDs for this show (only CONFIRMED bookings)
                List<Long> currentBookedSeatIds = em.createQuery(
                                "SELECT t.seat.id FROM Ticket t " +
                                        "WHERE t.show = :show " +
                                        "AND t.booking.status = 'CONFIRMED'",
                                Long.class)
                        .setParameter("show", selectedShow)
                        .getResultList();

                Platform.runLater(() -> {
                    bookedSeatIdsForThisShow = currentBookedSeatIds;
                    seatGridPane.getChildren().clear();
                    allSeatsMap.clear();

                    Map<String, Integer> rowMap = new HashMap<>();
                    int currentRowIndex = 0;

                    // Sort seats by row and number
                    allSeatsInScreen.sort(Comparator
                            .comparing((Seat s) -> s.getSeatRow())
                            .thenComparing(Seat::getSeatNumber));

                    String lastRow = "";
                    for (Seat seat : allSeatsInScreen) {
                        if (!seat.getSeatRow().equals(lastRow)) {
                            lastRow = seat.getSeatRow();
                            rowMap.put(lastRow, currentRowIndex++);
                        }
                        allSeatsMap.put(seat.getSeatRow() + seat.getSeatNumber(), seat);
                    }

                    // Create seat buttons
                    for (Seat seat : allSeatsInScreen) {
                        Button seatButton = new Button(seat.getSeatNumber().toString());
                        seatButton.setPrefSize(35, 35);
                        seatButton.setStyle("-fx-font-size: 10px; -fx-font-weight: bold;");

                        int gridRow = rowMap.get(seat.getSeatRow());
                        int gridCol = seat.getSeatNumber() - 1;

                        // Add seat button
                        seatGridPane.add(seatButton, gridCol + 1, gridRow + 1);

                        // Add row label (only for first seat in row)
                        if (gridCol == 0) {
                            Label rowLabel = new Label(seat.getSeatRow());
                            rowLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
                            seatGridPane.add(rowLabel, 0, gridRow + 1);
                        }

                        // Add column label (only for first row)
                        if (gridRow == 0) {
                            Label colLabel = new Label(String.valueOf(seat.getSeatNumber()));
                            colLabel.setStyle("-fx-font-weight: bold; -fx-text-fill: #7f8c8d;");
                            seatGridPane.add(colLabel, gridCol + 1, 0);
                        }

                        // Check if seat is booked for THIS SHOW specifically
                        if (bookedSeatIdsForThisShow.contains(seat.getId())) {
                            // Seat is booked for this show - grey and disabled
                            seatButton.setStyle("-fx-background-color: #95a5a6; -fx-text-fill: white; -fx-font-weight: bold;");
                            seatButton.setDisable(true);
                        } else {
                            // Seat is available - green and clickable
                            seatButton.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
                            seatButton.setOnAction(event -> toggleSeatSelection(seat, seatButton));
                        }
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() ->
                        showAlert(Alert.AlertType.ERROR, "Error", "Failed to load seats: " + e.getMessage())
                );
            } finally {
                // CRITICAL FIX: Always close EntityManager
                if (em != null && em.isOpen()) {
                    em.close();
                }
            }
        }).start();
    }

    private void toggleSeatSelection(Seat seat, Button button) {
        if (selectedSeats.contains(seat)) {
            selectedSeats.remove(seat);
            button.setStyle("-fx-background-color: #27ae60; -fx-text-fill: white; -fx-font-weight: bold;");
        } else {
            selectedSeats.add(seat);
            button.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-font-weight: bold;");
        }
        updateSelectedSeatsDisplay();
        updateTotalPrice();
    }

    private void updateSelectedSeatsDisplay() {
        selectedSeatsFlowPane.getChildren().clear();
        selectedSeats.stream()
                .sorted(Comparator.comparing((Seat s) -> s.getSeatRow())
                        .thenComparing(Seat::getSeatNumber))
                .forEach(seat -> {
                    Label seatLabel = new Label(seat.getSeatRow() + seat.getSeatNumber());
                    seatLabel.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 10; -fx-background-radius: 15; -fx-font-size: 12px;");
                    selectedSeatsFlowPane.getChildren().add(seatLabel);
                });
    }

    private void updateTotalPrice() {
        if (selectedShow != null && !selectedSeats.isEmpty()) {
            BigDecimal numberOfSeats = BigDecimal.valueOf(selectedSeats.size());
            BigDecimal ticketPrice = selectedShow.getTicketPrice();
            BigDecimal total = numberOfSeats.multiply(ticketPrice);
            totalPriceLabel.setText(String.format("₹%.2f", total));
        } else {
            totalPriceLabel.setText("₹0.00");
        }
    }

    @FXML
    private void handleConfirmBooking() {
        if (selectedSeats.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "No Seats Selected", "Please select at least one seat to book.");
            return;
        }

        if (selectedShow == null) {
            showAlert(Alert.AlertType.ERROR, "Booking Error", "No show is selected.");
            return;
        }

        final String userName = "GuestUser";
        BigDecimal numberOfSeats = BigDecimal.valueOf(selectedSeats.size());
        BigDecimal ticketPrice = selectedShow.getTicketPrice();
        final BigDecimal totalAmount = numberOfSeats.multiply(ticketPrice);

        // Store IDs before starting thread
        final List<Long> selectedSeatIds = new ArrayList<>();
        for (Seat seat : selectedSeats) {
            selectedSeatIds.add(seat.getId());
        }
        final Long showId = selectedShow.getId();

        new Thread(() -> {
            EntityManager entityManager = null;
            EntityTransaction transaction = null;

            try {
                entityManager = HibernateUtil.getEntityManager();
                transaction = entityManager.getTransaction();
                transaction.begin();

                System.out.println("=== Starting booking transaction ===");

                // Create booking
                Booking booking = new Booking(userName, LocalDateTime.now(), totalAmount);
                booking.setStatus("CONFIRMED");
                entityManager.persist(booking);
                entityManager.flush();

                System.out.println("Booking created with ID: " + booking.getId());

                // Get managed show
                Show managedShow = entityManager.find(Show.class, showId);
                if (managedShow == null) {
                    throw new RuntimeException("Show not found with ID: " + showId);
                }

                // Create tickets
                for (Long seatId : selectedSeatIds) {
                    Seat managedSeat = entityManager.find(Seat.class, seatId);
                    if (managedSeat == null) {
                        throw new RuntimeException("Seat not found with ID: " + seatId);
                    }

                    Ticket ticket = new Ticket(booking, managedShow, managedSeat, "CONFIRMED");
                    entityManager.persist(ticket);
                    System.out.println("Ticket created for seat: " + managedSeat.getSeatRow() + managedSeat.getSeatNumber());

                    managedSeat.setAvailable(false);
                    entityManager.merge(managedSeat);
                    System.out.println("Seat marked unavailable: " + managedSeat.getSeatRow() + managedSeat.getSeatNumber());
                }

                transaction.commit();
                System.out.println("=== Booking transaction committed successfully! ===");

                final Long bookingId = booking.getId();

                Platform.runLater(() -> {
                    showAlert(Alert.AlertType.INFORMATION, "Booking Confirmed",
                            String.format("Successfully booked %d seat(s) for %s.\nTotal: ₹%.2f\nBooking ID: #%d",
                                    selectedSeatIds.size(), selectedShow.getMovie().getTitle(), totalAmount, bookingId));
                    selectedSeats.clear();
                    updateSelectedSeatsDisplay();
                    updateTotalPrice();
                    loadSeats();
                });

            } catch (Exception e) {
                System.err.println("!!! Error during booking: " + e.getMessage());
                e.printStackTrace();

                if (transaction != null && transaction.isActive()) {
                    transaction.rollback();
                    System.out.println("Transaction rolled back");
                }

                final String errorMessage = e.getMessage() != null ? e.getMessage() : "Unknown error occurred";
                Platform.runLater(() ->
                        showAlert(Alert.AlertType.ERROR, "Booking Failed",
                                "Error: " + errorMessage + "\n\nPlease try again."));
            } finally {
                // CRITICAL FIX: Always close EntityManager
                if (entityManager != null && entityManager.isOpen()) {
                    entityManager.close();
                }
            }
        }).start();
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) seatGridPane.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
