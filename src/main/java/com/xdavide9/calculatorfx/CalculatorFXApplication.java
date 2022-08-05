/**
 * Copyright 2022 xDavide9
 * .
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 * .
 *        http://www.apache.org/licenses/LICENSE-2.0
 * .
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.xdavide9.calculatorfx;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

public class CalculatorFXApplication extends Application {

    @Override
    public void start(Stage stage) throws IOException {
        // this property makes sure grayscale AA is used to make the edges of text look smoother
        System.setProperty("prism.lcdtext", "false");
        FXMLLoader fxmlLoader = new FXMLLoader(CalculatorFXApplication.class.getResource("simpleOperations.fxml"));
        Scene scene = new Scene(fxmlLoader.load());

        try {
            scene.getStylesheets().add(Objects.requireNonNull(CalculatorFXApplication.class.getResource("darcula.css")).toExternalForm());
            System.out.println("Successfully set up stylesheet");
        } catch (NullPointerException e) {
            System.out.println("Could not set up stylesheet");
        }

        try {
            String path = Objects.requireNonNull(CalculatorFXApplication.class.getResource("icon.png")).toExternalForm();
            stage.getIcons().add(new Image(path));
            System.out.println("Successfully set up icon");
        } catch (NullPointerException e) {
            System.out.println("Could not set up icon");
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
