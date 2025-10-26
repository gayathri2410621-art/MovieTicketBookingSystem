package com.example.movieticketbookingsystem;

import com.example.movieticketbookingsystem.dao.MovieDao;
import com.example.movieticketbookingsystem.dao.MovieDaoImpl;
import com.example.movieticketbookingsystem.model.Movie;
import com.example.movieticketbookingsystem.model.Show;
import com.example.movieticketbookingsystem.view.MovieListController;
import com.example.movieticketbookingsystem.view.ShowDetailsController;
import com.example.movieticketbookingsystem.view.SeatSelectionController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;

public class MainController {

    @FXML
    private BorderPane mainPane;

    private MovieDao movieDao = new MovieDaoImpl();

    @FXML
    public void initialize() {
        // Load the initial view (e.g., movie list) when the main application starts
        loadMovieList();
    }

    public void loadMovieList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/movieticketbookingsystem/view/movie-list-view.fxml"));
            VBox movieListView = loader.load();

            MovieListController movieListController = loader.getController();
            movieListController.setMainController(this); // Pass a reference to the main controller

            mainPane.setCenter(movieListView); // Set the movie list as the center content
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load movie list: " + e.getMessage());
        }
    }

    // This method will be called from MovieListController to navigate to show details
    public void showMovieDetails(Movie movie) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/movieticketbookingsystem/view/show-details-view.fxml"));
            VBox showDetailsView = loader.load();

            ShowDetailsController showDetailsController = loader.getController();
            showDetailsController.setMainController(this);
            showDetailsController.setMovie(movie); // Pass the selected movie

            mainPane.setCenter(showDetailsView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load movie details: " + e.getMessage());
        }
    }

    // This method will be called from ShowDetailsController to navigate to seat selection
    public void showSeatSelection(Show show) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/movieticketbookingsystem/view/seat-selection-view.fxml"));
            VBox seatSelectionView = loader.load();

            SeatSelectionController seatSelectionController = loader.getController();
            seatSelectionController.setMainController(this);
            seatSelectionController.setShow(show); // Pass the selected show

            mainPane.setCenter(seatSelectionView);
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Navigation Error", "Could not load seat selection: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
