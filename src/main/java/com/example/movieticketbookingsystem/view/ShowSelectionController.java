package com.example.movieticketbookingsystem.view;

import com.example.movieticketbookingsystem.model.Movie;
import com.example.movieticketbookingsystem.model.Show;
import com.example.movieticketbookingsystem.util.HibernateUtil;
import jakarta.persistence.EntityManager;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.InputStream;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class ShowSelectionController {

    @FXML
    private VBox moviesContainer;

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("hh:mm a");
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, MMM d");

    @FXML
    public void initialize() {
        loadMoviesAndShows();
    }

    private void loadMoviesAndShows() {
        new Thread(() -> {
            EntityManager em = null;
            try {
                em = HibernateUtil.getEntityManager();

                List<Movie> movies = em.createQuery("SELECT DISTINCT m FROM Movie m", Movie.class)
                        .getResultList();

                Platform.runLater(() -> {
                    moviesContainer.getChildren().clear();

                    for (Movie movie : movies) {
                        moviesContainer.getChildren().add(createMovieCard(movie));
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
                Platform.runLater(() -> {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setContentText("Failed to load movies: " + e.getMessage());
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

    private VBox createMovieCard(Movie movie) {
        VBox movieCard = new VBox(15);
        movieCard.setStyle("-fx-background-color: white; -fx-padding: 20; -fx-background-radius: 10; -fx-border-color: #ddd; -fx-border-radius: 10;");
        movieCard.setMaxWidth(800);

        HBox header = new HBox(20);
        header.setAlignment(Pos.CENTER_LEFT);

        ImageView poster = new ImageView();
        poster.setFitWidth(120);
        poster.setFitHeight(180);
        poster.setPreserveRatio(true);
        poster.setImage(loadMovieImage(movie));

        VBox details = new VBox(8);
        Label title = new Label(movie.getTitle());
        title.setStyle("-fx-font-size: 22px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label genre = new Label(movie.getGenre() + " • " + movie.getDurationMinutes() + " mins");
        genre.setStyle("-fx-font-size: 14px; -fx-text-fill: #7f8c8d;");

        Label description = new Label(movie.getDescription());
        description.setWrapText(true);
        description.setPrefWidth(600);
        description.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");

        details.getChildren().addAll(title, genre, description);
        header.getChildren().addAll(poster, details);

        VBox showsBox = loadShowsForMovie(movie);

        movieCard.getChildren().addAll(header, new Separator(), showsBox);

        return movieCard;
    }

    private VBox loadShowsForMovie(Movie movie) {
        VBox showsBox = new VBox(10);

        EntityManager em = null;
        try {
            em = HibernateUtil.getEntityManager();

            List<Show> shows = em.createQuery(
                            "SELECT s FROM Show s " +
                                    "JOIN FETCH s.screen sc " +
                                    "JOIN FETCH sc.theater " +
                                    "WHERE s.movie.id = :movieId " +
                                    "ORDER BY s.showTime",
                            Show.class)
                    .setParameter("movieId", movie.getId())
                    .getResultList();

            if (shows.isEmpty()) {
                Label noShows = new Label("No shows available");
                noShows.setStyle("-fx-text-fill: #95a5a6; -fx-font-size: 14px;");
                showsBox.getChildren().add(noShows);
            } else {
                Label showsLabel = new Label("Available Shows:");
                showsLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 16px;");
                showsBox.getChildren().add(showsLabel);

                FlowPane showsFlow = new FlowPane(15, 15);
                for (Show show : shows) {
                    showsFlow.getChildren().add(createShowButton(show));
                }
                showsBox.getChildren().add(showsFlow);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Label errorLabel = new Label("Error loading shows");
            errorLabel.setStyle("-fx-text-fill: #e74c3c; -fx-font-size: 14px;");
            showsBox.getChildren().add(errorLabel);
        } finally {
            // CRITICAL FIX: Always close EntityManager
            if (em != null && em.isOpen()) {
                em.close();
            }
        }

        return showsBox;
    }

    private Button createShowButton(Show show) {
        VBox showInfo = new VBox(5);
        showInfo.setAlignment(Pos.CENTER);
        showInfo.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 8; -fx-cursor: hand;");
        showInfo.setPrefWidth(180);

        Label time = new Label(show.getShowTime().format(TIME_FORMATTER));
        time.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label date = new Label(show.getShowTime().format(DATE_FORMATTER));
        date.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Label theater = new Label(show.getScreen().getTheater().getName());
        theater.setStyle("-fx-font-size: 13px; -fx-text-fill: #34495e;");

        Label screen = new Label("Screen " + show.getScreen().getScreenNumber());
        screen.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d;");

        Label price = new Label("₹" + show.getTicketPrice());
        price.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;");

        showInfo.getChildren().addAll(time, date, theater, screen, price);

        Button btn = new Button();
        btn.setGraphic(showInfo);
        btn.setStyle("-fx-background-color: transparent; -fx-padding: 0;");

        btn.setOnAction(e -> openSeatSelection(show));

        btn.setOnMouseEntered(e ->
                showInfo.setStyle("-fx-background-color: #d5dbdb; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #3498db; -fx-border-width: 2; -fx-border-radius: 8; -fx-cursor: hand;"));

        btn.setOnMouseExited(e ->
                showInfo.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 15; -fx-background-radius: 8; -fx-border-color: #bdc3c7; -fx-border-radius: 8; -fx-cursor: hand;"));

        return btn;
    }

    private void openSeatSelection(Show show) {
        EntityManager em = null;
        try {
            // Re-load the show with all relationships EAGER fetched
            em = HibernateUtil.getEntityManager();

            Show fullShow = em.createQuery(
                            "SELECT s FROM Show s " +
                                    "JOIN FETCH s.movie " +
                                    "JOIN FETCH s.screen sc " +
                                    "JOIN FETCH sc.theater " +
                                    "WHERE s.id = :showId",
                            Show.class)
                    .setParameter("showId", show.getId())
                    .getSingleResult();

            // CRITICAL FIX: Close EntityManager BEFORE opening new window
            em.close();
            em = null; // Set to null so finally block doesn't try to close again

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/movieticketbookingsystem/view/seat-selection-view.fxml"));
            Parent root = loader.load();

            SeatSelectionController controller = loader.getController();
            controller.setShow(fullShow);  // Pass fully loaded show

            Stage stage = new Stage();
            stage.setTitle("Select Seats - " + fullShow.getMovie().getTitle());
            stage.setScene(new Scene(root, 1000, 700));
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Could not open seat selection");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        } finally {
            // CRITICAL FIX: Always close EntityManager
            if (em != null && em.isOpen()) {
                em.close();
            }
        }
    }

    private Image loadMovieImage(Movie movie) {
        if (movie.getImageUrl() != null && !movie.getImageUrl().isEmpty()) {
            try (InputStream stream = getClass().getResourceAsStream("/com/example/movieticketbookingsystem/images/" + movie.getImageUrl())) {
                if (stream != null) {
                    return new Image(stream);
                }
            } catch (Exception e) {
                System.err.println("Failed to load image: " + e.getMessage());
            }
        }

        return new Image("https://via.placeholder.com/120x180?text=No+Poster");
    }

    @FXML
    private void handleBack() {
        Stage stage = (Stage) moviesContainer.getScene().getWindow();
        stage.close();
    }
}
