package com.example.movieticketbookingsystem.view;

import com.example.movieticketbookingsystem.MainController;
import com.example.movieticketbookingsystem.dao.MovieDao;
import com.example.movieticketbookingsystem.dao.MovieDaoImpl;
import com.example.movieticketbookingsystem.model.Movie;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;
import java.io.InputStream;
import java.util.List;

public class MovieListController {

    @FXML
    private VBox movieContainer;

    private MovieDao movieDao = new MovieDaoImpl();
    private MainController mainController; // Reference to main controller

    private static final String DEFAULT_POSTER_PATH = "/com/example/movieticketbookingsystem/images/default_poster.jpg";
    private static final String MOVIE_POSTER_RESOURCE_BASE_PATH = "/com/example/movieticketbookingsystem/images/";

    // THIS METHOD WAS MISSING - Now added!
    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    public void initialize() {
        loadMovies();
    }

    private void loadMovies() {
        new Thread(() -> {
            List<Movie> movies = movieDao.findAll();
            Platform.runLater(() -> {
                movieContainer.getChildren().clear();
                if (movies.isEmpty()) {
                    Label noMoviesLabel = new Label("No movies available.");
                    noMoviesLabel.setStyle("-fx-font-size: 16px; -fx-text-fill: #7f8c8d;");
                    movieContainer.getChildren().add(noMoviesLabel);
                } else {
                    for (Movie movie : movies) {
                        movieContainer.getChildren().add(createMovieCard(movie));
                    }
                }
            });
        }).start();
    }

    private HBox createMovieCard(Movie movie) {
        HBox movieCard = new HBox(15);
        movieCard.setAlignment(Pos.CENTER_LEFT);
        movieCard.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10; -fx-cursor: hand;");
        movieCard.setMaxWidth(850);

        // Poster ImageView
        ImageView poster = new ImageView();
        poster.setFitWidth(100);
        poster.setFitHeight(150);
        poster.setPreserveRatio(true);

        // Load movie image
        Image movieImage = loadMovieImage(movie);
        poster.setImage(movieImage);

        // Movie details VBox
        VBox details = new VBox(5);

        Label title = new Label(movie.getTitle());
        title.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label genre = new Label("Genre: " + movie.getGenre());
        genre.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Label duration = new Label("Duration: " + movie.getDurationMinutes() + " mins");
        duration.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Label releaseDate = new Label("Release: " + movie.getReleaseDate());
        releaseDate.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        details.getChildren().addAll(title, genre, duration, releaseDate);

        movieCard.getChildren().addAll(poster, details);

        // Click handler - Show description OR navigate to details
        movieCard.setOnMouseClicked(event -> {
            System.out.println("Movie clicked: " + movie.getTitle()); // Debug

            // Option 1: Show popup with description (Simple)
            showMovieDescription(movie);

            // Option 2: Navigate to full details screen (if you prefer)
            // if (mainController != null) {
            //     mainController.showMovieDetails(movie);
            // }
        });

        // Hover effects
        movieCard.setOnMouseEntered(e ->
                movieCard.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 10; -fx-cursor: hand;")
        );
        movieCard.setOnMouseExited(e ->
                movieCard.setStyle("-fx-background-color: white; -fx-padding: 15; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10; -fx-cursor: hand;")
        );

        return movieCard;
    }

    private Image loadMovieImage(Movie movie) {
        Image movieImage = null;

        if (movie.getImageUrl() != null && !movie.getImageUrl().isEmpty()) {
            System.out.println("Trying to load image: " + movie.getImageUrl() + " for movie: " + movie.getTitle());

            try {
                InputStream resourceStream = getClass().getResourceAsStream(MOVIE_POSTER_RESOURCE_BASE_PATH + movie.getImageUrl());
                if (resourceStream != null) {
                    movieImage = new Image(resourceStream);
                    System.out.println("✓ Loaded image from resources: " + movie.getImageUrl());
                } else {
                    System.err.println("✗ Image not found in resources: " + MOVIE_POSTER_RESOURCE_BASE_PATH + movie.getImageUrl());

                    File imageFile = new File(movie.getImageUrl());
                    if (imageFile.exists() && imageFile.isFile()) {
                        movieImage = new Image(imageFile.toURI().toString());
                        System.out.println("✓ Loaded image from file system");
                    }
                }
            } catch (Exception e) {
                System.err.println("Failed to load image for movie: " + movie.getTitle() + " - " + e.getMessage());
            }
        }

        if (movieImage == null || movieImage.isError()) {
            try (InputStream defaultStream = getClass().getResourceAsStream(DEFAULT_POSTER_PATH)) {
                if (defaultStream != null) {
                    movieImage = new Image(defaultStream);
                    System.out.println("Using default poster for: " + movie.getTitle());
                } else {
                    System.err.println("Default poster image not found at: " + DEFAULT_POSTER_PATH);
                    movieImage = new Image("https://via.placeholder.com/100x150?text=No+Poster");
                }
            } catch (Exception e) {
                System.err.println("Failed to load default poster: " + e.getMessage());
                movieImage = new Image("https://via.placeholder.com/100x150?text=No+Poster");
            }
        }

        return movieImage;
    }

    private void showMovieDescription(Movie movie) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(movie.getTitle());
        alert.setHeaderText(String.format("%s • %d mins • %s",
                movie.getGenre(),
                movie.getDurationMinutes(),
                movie.getReleaseDate()));

        // Use TextArea for scrollable description
        TextArea textArea = new TextArea(movie.getDescription());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(6);
        textArea.setPrefColumnCount(50);

        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    @FXML
    private void handleBack() {
        if (movieContainer.getScene().getWindow() instanceof Stage) {
            Stage stage = (Stage) movieContainer.getScene().getWindow();
            stage.close();
        }
    }

    @FXML
    private void handleRefresh() {
        loadMovies();
    }
}
