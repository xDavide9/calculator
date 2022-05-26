package com.xdavide9.calculatorfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class CalculatorFXApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // this property makes sure grayscale AA is used to make the edges of text look smoother
        System.setProperty("prism.lcdtext", "false");
        FXMLLoader fxmlLoader = new FXMLLoader(CalculatorFXApplication.class.getResource("simpleOperations.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        try {
            scene.getStylesheets().add(Objects.requireNonNull(CalculatorFXApplication.class.getResource("darcula.css")).toExternalForm());
            log.info("Successfully set up stylesheet");
        } catch (NullPointerException e) {
            log.error("Could not set up stylesheet", e);
        }

        try {
            String path = Objects.requireNonNull(CalculatorFXApplication.class.getResource("icon.png")).toExternalForm();
            stage.getIcons().add(new Image(path));
            log.info("Successfully set up icon");
        } catch (NullPointerException e) {
            log.error("Could not set up icon", e);
        }

        stage.setTitle("CalculatorFX");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}
