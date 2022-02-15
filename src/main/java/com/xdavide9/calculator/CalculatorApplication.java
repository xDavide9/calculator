package com.xdavide9.calculator;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Objects;

public class CalculatorApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        //this property makes sure grayscale AA is used to make the edges of text look smoother
        System.setProperty("prism.lcdtext", "false");
        FXMLLoader fxmlLoader = new FXMLLoader(CalculatorApplication.class.getResource("calculatorSimple.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Calculator");
        scene.getStylesheets().add(Objects.requireNonNull(CalculatorApplication.class.getResource("darcula.css")).toExternalForm());
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}