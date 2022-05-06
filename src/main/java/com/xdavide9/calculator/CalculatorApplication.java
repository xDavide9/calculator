package com.xdavide9.calculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.Objects;

@Slf4j
public class CalculatorApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // this property makes sure grayscale AA is used to make the edges of text look smoother
        System.setProperty("prism.lcdtext", "false");
        FXMLLoader fxmlLoader = new FXMLLoader(CalculatorApplication.class.getResource("calculatorSimple.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        // stylesheet
        try {
            scene.getStylesheets().add(Objects.requireNonNull(CalculatorApplication.class.getResource("darcula.css")).toExternalForm());
            log.info("Successfully set up stylesheet");
        } catch (NullPointerException e) {
            log.error("Could not set up stylesheet", e);
        }

        // icon
        try {
            String path = Objects.requireNonNull(CalculatorApplication.class.getResource("icon.png")).toExternalForm();
            stage.getIcons().add(new Image(path));
            log.info("Successfully set up icon");
        } catch (NullPointerException e) {
            log.error("Could not set up icon", e);
        }

        stage.setTitle("FXCalculator");
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}