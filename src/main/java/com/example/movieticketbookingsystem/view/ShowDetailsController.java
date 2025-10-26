package com.example.movieticketbookingsystem.view;

import com.example.movieticketbookingsystem.MainController;
import com.example.movieticketbookingsystem.dao.ShowDao;
import com.example.movieticketbookingsystem.dao.ShowDaoImpl;
import com.example.movieticketbookingsystem.model.Movie;
import com.example.movieticketbookingsystem.model.Show;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.File;
import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShowDetailsController {

    @FXML
    private ImageView moviePoster;
    @FXML
    private Label movieTitleLabel;
    @FXML
    private Label movieGenreLabel;
    @FXML
    private Label movieDurationLabel;
    @FXML
    private Label movieDescriptionLabel;
    @FXML
    private VBox showsContainer;

    private Movie selectedMovie;
    private ShowDao showDao = new ShowDaoImpl();
    private MainController mainController;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("EEEE, MMM d, yyyy 'at' hh:mm a");
    private static final String DEFAULT_POSTER_PATH = "/com/example/movieticketbookingsystem/images/default_poster.jpg";
    private static final String MOVIE_POSTER_RESOURCE_BASE_PATH = "/com/example/movieticketbookingsystem/images/";


    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setMovie(Movie movie) {
        this.selectedMovie = movie;
        updateUI();
        loadShows();
    }

    private void updateUI() {
        if (selectedMovie != null) {
            movieTitleLabel.setText(selectedMovie.getTitle());
            movieGenreLabel.setText("Genre: " + selectedMovie.getGenre());
            movieDurationLabel.setText("Duration: " + selectedMovie.getDurationMinutes() + " mins");
            movieDescriptionLabel.setText(selectedMovie.getDescription());

            // --- Image Loading Logic ---
            Image movieImage = null;
            if (selectedMovie.getImageUrl() != null && !selectedMovie.getImageUrl().isEmpty()) {
                try {
                    // Try loading as a resource first
                    InputStream resourceStream = getClass().getResourceAsStream(MOVIE_POSTER_RESOURCE_BASE_PATH + selectedMovie.getImageUrl());
                    if (resourceStream != null) {
                        movieImage = new Image(resourceStream);
                    } else {
                        // If not found as resource, try loading as a file path
                        File imageFile = new File(selectedMovie.getImageUrl());
                        if (imageFile.exists() && imageFile.isFile()) {
                            movieImage = new Image(imageFile.toURI().toString());
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load image for movie: " + selectedMovie.getTitle() + " from URL/Path: " + selectedMovie.getImageUrl() + " - " + e.getMessage());
                }
            }

            // If no specific image loaded, use default
            if (movieImage == null || movieImage.isError()) {
                try (InputStream defaultStream = getClass().getResourceAsStream(DEFAULT_POSTER_PATH)) {
                    if (defaultStream != null) {
                        movieImage = new Image(defaultStream);
                    } else {
                        System.err.println("Default poster image not found at: " + DEFAULT_POSTER_PATH);
                        movieImage = new Image("https://via.placeholder.com/150x225?text=No+Poster");
                    }
                } catch (Exception e) {
                    System.err.println("Failed to load default poster: " + e.getMessage());
                    movieImage = new Image("https://via.placeholder.com/150x225?text=No+Poster");
                }
            }
            moviePoster.setImage(movieImage);
            // --- End Image Loading Logic ---
        }
    }

    private void loadShows() {
        if (selectedMovie == null) {
            return;
        }

        new Thread(() -> {
            // CRITICAL FIX: Ensure ShowDao closes its EntityManager properly
            // If ShowDao doesn't close EM, update ShowDaoImpl to use try-finally
            List<Show> shows = showDao.findShowsByMovie(selectedMovie);

            Platform.runLater(() -> {
                showsContainer.getChildren().clear();
                if (shows.isEmpty()) {
                    showsContainer.getChildren().add(new Label("No shows scheduled for this movie."));
                } else {
                    for (Show show : shows) {
                        showsContainer.getChildren().add(createShowCard(show));
                    }
                }
            });
        }).start();
    }

    private HBox createShowCard(Show show) {
        HBox showCard = new HBox(15);
        showCard.setAlignment(Pos.CENTER_LEFT);
        showCard.getStyleClass().add("show-card");
        showCard.setMaxWidth(800);

        VBox details = new VBox(5);
        Label timeLabel = new Label(show.getShowTime().format(DATE_TIME_FORMATTER));
        timeLabel.getStyleClass().add("show-time");
        Label screenLabel = new Label("Screen: " + show.getScreen().getScreenNumber() + " (" + show.getScreen().getTheater().getName() + ")");
        Label priceLabel = new Label(String.format("Price: $%.2f", show.getTicketPrice()));

        details.getChildren().addAll(timeLabel, screenLabel, priceLabel);

        Button selectButton = new Button("Select Seats");
        selectButton.getStyleClass().add("select-button");
        selectButton.setOnAction(event -> {
            if (mainController != null) {
                mainController.showSeatSelection(show);
            }
        });

        // Use HBox.Hgrow to push the button to the right
        HBox spacer = new HBox();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);

        showCard.getChildren().addAll(details, spacer, selectButton);
        return showCard;
    }

    @FXML
    private void handleBackToMovies() {
        if (mainController != null) {
            mainController.loadMovieList();
        }
    }
}
