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
import java.math.MathContext;
import java.math.RoundingMode;

public class CalculatorController {

    @FXML
    private Label label;

    //all the members must be static because they are shared among the two instances
    //of this class created for calculatorComplex.fxml and calculatorSimple.fxml

    private static BigDecimal count, temp;
    private static char operation;
    private static boolean hasCount, hasTemp;
    private static StringBuilder builder = new StringBuilder();
    private static String onLabel;
    private static boolean isOnSimple = true;
    private static final int maxDigits = 11;

    private void setDefaults() {
        count = new BigDecimal("0");
        temp = new BigDecimal("0");
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
        System.out.println(id);

        if (onLabel.equals("0")) {
            builder = new StringBuilder();
        }

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
     *
     * Currently, performed operations:
     * Addition
     * Subtraction
     * Multiplication
     * Division
     * To The Power Of N
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
        System.out.println(id);

        if (label.getText().equals(""))
            return;

        if (!hasCount) {
            count = new BigDecimal(label.getText());
            hasCount = true;
        } else {
            temp = new BigDecimal(label.getText());
            hasTemp = true;
        }

        builder = new StringBuilder();

        //might give bugs
        onLabel = String.valueOf(count);
        label.setText(onLabel);

        if (hasTemp) {
            if (operation != 0) {
                switch (operation) {
                    case '+' ->  {
                        count = count.add(temp);
                        count = count.stripTrailingZeros();
                    }
                    case '-' ->  {
                        count = count.subtract(temp);
                        count = count.stripTrailingZeros();
                    }
                    case '*' -> {
                        count = count.setScale(15, RoundingMode.HALF_UP);
                        count = count.multiply(temp);
                        //do the operation before with a high scale and then scale it based on the number of left digits
                        //in order to be in range of the max digits that can be displayed
                        int leftDigits = count.toString().substring(0, count.toString().indexOf('.')).length();
                        count = count.setScale(maxDigits - leftDigits - 2, RoundingMode.HALF_UP);
                        //-2 is because 1 is for the point . and 1 is for leaving a space to use +/- function
                        count = count.stripTrailingZeros();
                        //removes the extra zeros that don't make a difference in the number
                    }
                    case '/' ->  {
                        count = count.setScale(15, RoundingMode.HALF_UP);
                        count = count.divide(temp, RoundingMode.HALF_UP);
                        int leftDigits = count.toString().substring(0, count.toString().indexOf('.')).length();
                        count = count.setScale(maxDigits - leftDigits - 2, RoundingMode.HALF_UP);
                        //-2 is because 1 is for the point . and 1 is for leaving a space to use +/- function
                        count = count.stripTrailingZeros();
                        //removes the extra zeros that don't make a difference in the number
                    }
                    case 'n' -> {   //to the power of n
                        count = BigDecimal.valueOf(Math.pow(count.doubleValue(), temp.doubleValue()));
                        int leftDigits = count.toString().substring(0, count.toString().indexOf('.')).length();
                        count = count.setScale(maxDigits - leftDigits - 2, RoundingMode.HALF_UP);
                        count = count.stripTrailingZeros();
                    }
                }
                hasTemp = false;
                temp = new BigDecimal("0");
            }
        }

        switch (id) {
            case "plusButton" -> operation = '+';
            case "minusButton" -> operation = '-';
            case "timesButton" -> operation = '*';
            case "divideButton" -> operation = '/';
            case "toThePowerOfNButton" -> operation = 'n';
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

            temp = new BigDecimal(label.getText());
            switch (operation) {
                case '+' ->  {
                    count = count.add(temp);
                    count = count.stripTrailingZeros();
                }
                case '-' ->  {
                    count = count.subtract(temp);
                    count = count.stripTrailingZeros();
                }
                case '*' -> {
                    count = count.setScale(15, RoundingMode.HALF_UP);
                    count = count.multiply(temp);
                    int leftDigits = count.toString().substring(0, count.toString().indexOf('.')).length();
                    count = count.setScale(maxDigits - leftDigits - 2, RoundingMode.HALF_UP);
                    //-2 is because 1 is for the point . and 1 is for leaving a space to use +/- function
                    count = count.stripTrailingZeros();
                    //removes the extra zeros that don't make a difference in the number
                }
                case '/' ->  {
                    count = count.setScale(15, RoundingMode.HALF_UP);
                    count = count.divide(temp, RoundingMode.HALF_UP);
                    int leftDigits = count.toString().substring(0, count.toString().indexOf('.')).length();
                    count = count.setScale(maxDigits - leftDigits - 2, RoundingMode.HALF_UP);
                    //-2 is because 1 is for the point . and 1 is for leaving a space to use +/- function
                    count = count.stripTrailingZeros();
                    //removes the extra zeros that don't make a difference in the number
                }
                case 'n' -> {   //to the power of n
                    count = BigDecimal.valueOf(Math.pow(count.doubleValue(), temp.doubleValue()));
                    int leftDigits = count.toString().substring(0, count.toString().indexOf('.')).length();
                    count = count.setScale(maxDigits - leftDigits - 2, RoundingMode.HALF_UP);
                    count = count.stripTrailingZeros();
                }
            }
            operation = 0;
            temp = new BigDecimal("0");
            hasTemp = false;
        } else {
            return;
        }

        builder = new StringBuilder();
        onLabel = String.valueOf(count);
        if (onLabel.length() > maxDigits)
            onLabel = "err";
        label.setText(onLabel);

        count = new BigDecimal("0");
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
        BigDecimal value = new BigDecimal(onLabel);
        value = value.multiply(new BigDecimal("-1"));
        value = value.stripTrailingZeros();
        onLabel = String.valueOf(value);
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

    /**
     * more operation performed singularly on the value being displayed already
     * without the need to access either count or temp because you get the result immediately
     * without pressing equal, like an instant change, so just change the number according to the operation
     *
     * Currently, performed operations:
     * To The Power Of Two
     * Square root
     */
    @FXML
    protected void onMoreOperationPressed(ActionEvent e) {
        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            setDefaults();
            return;
        }

        String id = ((Node) e.getSource()).getId();
        System.out.println(id);

        BigDecimal value = new BigDecimal(onLabel);
        switch (id) {
            case "toThePowerOfTwoButton" -> value = value.pow(2);
            case "radicalTwoButton" -> value = value.sqrt(new MathContext(15));
        }

        if (value.toString().contains(".")) {
            int leftDigits = value.toString().substring(0, value.toString().indexOf('.')).length();
            value = value.setScale(maxDigits - leftDigits - 2, RoundingMode.HALF_UP);
            value = value.stripTrailingZeros();
        }

        onLabel = String.valueOf(value);
        if (onLabel.length() > maxDigits)
            onLabel = "err";

        builder = new StringBuilder();
        builder.append(onLabel);
        label.setText(onLabel);
    }

}