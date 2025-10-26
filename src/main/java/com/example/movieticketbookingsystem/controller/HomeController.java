package com.example.movieticketbookingsystem.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;

import java.io.IOException;

public class HomeController {

    @FXML
    public void initialize() {
        System.out.println("🎬 G Cube Cinema - Home Screen Loaded");
    }

    @FXML
    private void handleViewMovies() {
        try {
            FXMLLoader loader = new FXMLLoader();
            java.net.URL fxmlLocation = getClass().getResource("/com/example/movieticketbookingsystem/view/movie-list-view.fxml");

            if (fxmlLocation == null) {
                showError("File Not Found", "Could not find movie-list-view.fxml.");
                return;
            }

            loader.setLocation(fxmlLocation);
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Browse Movies - G Cube");
            stage.setScene(new Scene(root, 900, 600));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not open Movies screen:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleNewBooking() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/movieticketbookingsystem/view/show-selection-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Select Show - G Cube");
            stage.setScene(new Scene(root, 950, 700));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not open show selection:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleViewBookings() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/movieticketbookingsystem/view/my-bookings-view.fxml"));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("My Bookings - G Cube");
            stage.setScene(new Scene(root, 900, 700));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showError("Navigation Error", "Could not open bookings screen:\n" + e.getMessage());
        }
    }

    @FXML
    private void handleAbout() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("About G Cube");
        alert.setHeaderText("🎬 G CUBE Cinema");
        alert.setContentText("Version 1.0\n\n" +
                "Premium Movie Booking Experience\n\n" +
                "Developed with JavaFX 21 and Oracle Database 21c\n" +
                "Using Hibernate ORM for data persistence\n\n" +
                "Features:\n" +
                "• Browse Movies\n" +
                "• Book Tickets\n" +
                "• Interactive Seat Selection\n" +
                "• Booking Management\n\n" +
                "© 2025 G Cube Cinema");
        alert.showAndWait();
    }

    @FXML
    private void handleExit() {
        System.out.println("👋 Exiting G Cube Cinema...");
        System.exit(0);
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
