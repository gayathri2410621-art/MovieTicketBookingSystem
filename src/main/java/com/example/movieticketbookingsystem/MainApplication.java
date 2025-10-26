package com.example.movieticketbookingsystem;

import com.example.movieticketbookingsystem.util.HibernateUtil;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

public class MainApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(
                getClass().getResource("/com/example/movieticketbookingsystem/home-view.fxml")
        );

        Scene scene = new Scene(fxmlLoader.load(), 900, 600);

        // Add CSS stylesheet (if it exists)
        String css = getClass().getResource("/com/example/movieticketbookingsystem/style.css").toExternalForm();
        scene.getStylesheets().add(css);

        stage.setTitle("Movie Ticket Booking System");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }

    @Override
    public void stop() throws Exception {
        // Shutdown Hibernate when the application closes
        HibernateUtil.shutdown();
        super.stop();
    }
}
