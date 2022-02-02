package com.xdavide9.calculator;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class CalculatorApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        //this property makes sure grayscale AA is used to make the edges of text look smoother
        System.setProperty("prism.lcdtext", "false");
        FXMLLoader fxmlLoader = new FXMLLoader(CalculatorApplication.class.getResource("calculatorSimple.fxml"));
        Scene scene = new Scene(fxmlLoader.load());
        stage.setTitle("Calculator");
        DarculaFX.applyDarculaStyle(scene);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}