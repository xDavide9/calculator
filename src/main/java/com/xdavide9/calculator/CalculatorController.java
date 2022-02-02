package com.xdavide9.calculator;

import com.github.mouse0w0.darculafx.DarculaFX;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.stage.Stage;

import java.io.IOException;
import java.math.BigDecimal;

public class CalculatorController {

    @FXML
    private Label label;

    //all the members must be static because they are shared among the two instances
    //of this class created for calculatorComplex.fxml and calculatorSimple.fxml

    //todo figure out why the compiler is not giving the simple result of 2 to the operation of 5.6 minus 3
    //todo something that has to with double types in any operation anyways wtf

    private static double count, temp;
    private static char operation;
    private static boolean hasCount, hasTemp;
    private static StringBuilder builder = new StringBuilder();
    private static String onLabel;
    private static boolean isOnSimple = true;
    private static final int maxDigits = 11;

    private void setDefaults() {
        count = 0;
        temp = 0;
        operation = 0;
        hasCount = false;
        hasTemp = false;
        onLabel = "0";
        label.setText(onLabel);
        builder = new StringBuilder();
    }

    /**
     * appends to the label the number of the corresponding button
     */
    @FXML
    protected void OnNumberPressed(ActionEvent e) {
        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            setDefaults();
            return;
        }

        String id = ((Node) e.getSource()).getId();

        switch (id) {
            case "zeroButton" ->  {
                if (onLabel.startsWith("0") && (!onLabel.contains("."))) {
                    return;
                }
                builder.append(0);
            }
            case "oneButton" -> builder.append(1);
            case "twoButton" -> builder.append(2);
            case "threeButton" -> builder.append(3);
            case "fourButton" -> builder.append(4);
            case "fiveButton" -> builder.append(5);
            case "sixButton" -> builder.append(6);
            case "sevenButton" -> builder.append(7);
            case "eightButton" -> builder.append(8);
            case "nineButton" -> builder.append(9);
        }

        onLabel = builder.toString();
        if (onLabel.length() > maxDigits)
            onLabel = onLabel.substring(0, maxDigits);

        label.setText(onLabel);
    }

    /**
     * stores the number in the label either in count or temp
     * performs an operation if it was stored in memory
     * stores the operation to be performed next
     */
    @FXML
    protected void onOperationPressed(ActionEvent e) {
        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            setDefaults();
            return;
        }

        String id = ((Node) e.getSource()).getId();

        if (label.getText().equals(""))
            return;

        if (!hasCount) {
            count = Double.parseDouble(label.getText());
            hasCount = true;
        } else {
            temp = Double.parseDouble(label.getText());
            hasTemp = true;
        }

        builder = new StringBuilder();

        //might give bugs
        onLabel = String.valueOf(count).endsWith(".0") ?
                String.valueOf(count).substring(0, String.valueOf(count).length() - 2) :
                String.valueOf(count);
        label.setText(onLabel);

        if (hasTemp) {
            if (operation != 0) {
                switch (operation) {
                    case '+' -> count = count + temp;
                    case '-' -> count = count - temp;
                    case '*' -> count = count * temp;
                    case '/' -> count = count / temp;
                }
                hasTemp = false;
                temp = 0;
            }
        }

        switch (id) {
            case "plusButton" -> operation = '+';
            case "minusButton" -> operation = '-';
            case "timesButton" -> operation = '*';
            case "divideButton" -> operation = '/';
        }
    }

    /**
     * performs an operation if it was stored in memory
     * and displays the final value of the expression evaluated so far
     */
    @FXML
    protected void onEqualsPressed() {
        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            setDefaults();
            return;
        }

        if (operation != 0) {
            if (label.getText().equals(""))
                return;

            temp = Double.parseDouble(label.getText());
            switch (operation) {
                case '+' -> count = count + temp;
                case '-' -> count = count - temp;
                case '*' -> count = count * temp;
                case '/' -> count = count / temp;
            }
            operation = 0;
            temp = 0;
            hasTemp = false;
        } else {
            //might give bugs
            return;
        }

        builder = new StringBuilder();
        onLabel = String.valueOf(count).endsWith(".0") ?
                    String.valueOf(count).substring(0, String.valueOf(count).length() - 2) :
                        String.valueOf(count);
        if (onLabel.length() > maxDigits)
            onLabel = "err";
        label.setText(onLabel);

        count = 0;
        hasCount = false;
    }

    /**
     * appends a point to the label
     */
    @FXML
    protected void onPointPressed() {
        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            setDefaults();
            return;
        }

        if (onLabel.contains("."))
            return;
        builder = new StringBuilder();
        builder.append(onLabel);
        builder.append(".");
        onLabel = builder.toString();
        label.setText(onLabel);
    }

    /**
     * deletes a single digit in the label
     * without affecting anything in memory
     */
    @FXML
    protected void onDelPressed() {
        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            setDefaults();
            return;
        }

        if (onLabel.equals("0"))
            return;
        builder = new StringBuilder();
        onLabel = onLabel.substring(0, onLabel.length() - 1);
        if (onLabel.equals(""))
            onLabel = "0";
        builder.append(onLabel);
        label.setText(onLabel);
    }

    /**
     * deletes everything in the label
     * and removes any previous operation in memory
     */
    @FXML
    protected void onAcPressed() {
        setDefaults();
    }

    /**
     * changes the sign of the number in label
     */
    @FXML
    protected void onPlusMinusPressed() {
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            setDefaults();
            return;
        }
        if (onLabel.equals("0"))
            return;
        double value = Double.parseDouble(onLabel) * -1;
        onLabel = String.valueOf(value).endsWith(".0") ?
                    String.valueOf(value).substring(0, String.valueOf(value).length() - 2) :
                        String.valueOf(value);
        if (onLabel.length() > maxDigits)
            onLabel = "err";
        label.setText(onLabel);
    }

    /**
     * switches to a different scene where more
     * complex functions are present
     */
    @FXML
    protected void onFnPressed(ActionEvent e) throws IOException {
        FXMLLoader fxmlLoader;
        Stage stage;
        Scene scene;
        CalculatorController controller;
        onLabel = label.getText();

        if (isOnSimple) {
            isOnSimple = false;
            fxmlLoader = new FXMLLoader(CalculatorApplication.class.getResource("calculatorComplex.fxml"));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            scene = new Scene(fxmlLoader.load());
            DarculaFX.applyDarculaStyle(scene);
            //injecting the label's text through the controller each time you switch scene because it cannot be made static
            controller = fxmlLoader.getController();
            controller.label.setText(onLabel);
            stage.setScene(scene);
            stage.show();
        } else {
            isOnSimple = true;
            fxmlLoader = new FXMLLoader(CalculatorApplication.class.getResource("calculatorSimple.fxml"));
            stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
            scene = new Scene(fxmlLoader.load());
            DarculaFX.applyDarculaStyle(scene);
            //injecting the label's text through the controller each time you switch scene because it cannot be made static
            controller = fxmlLoader.getController();
            controller.label.setText(onLabel);
            stage.setScene(scene);
            stage.show();
        }
    }

    @FXML
    protected void onToThePowerOfTwoPressed() {
        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            setDefaults();
            return;
        }

        double temp = Math.pow(Double.parseDouble(label.getText()), 2);
        onLabel = String.valueOf(temp).endsWith(".0") ?
                String.valueOf(temp).substring(0, String.valueOf(temp).length() - 2) :
                String.valueOf(temp);
        if (onLabel.length() > maxDigits)
            onLabel = "err";
        label.setText(onLabel);
    }
}