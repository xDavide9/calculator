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

    //the members are static else upon injection the 2 fxml files would use different objects instead
    private static BigDecimal count, temp;
    private static char operation;
    private static boolean hasCount, hasTemp;
    private static StringBuilder builder = new StringBuilder();
    private static String onLabel;
    private static boolean isOnSimple = true;

    //some constants
    private static final int maxDigits = 11;
    private static final String errorString = "err";

    /**
     * appends to the label the number of the corresponding button
     */
    @FXML
    protected void OnNumberPressed(ActionEvent e) {
        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            System.err.println("resetting");
            setDefaults();
            return;
        }

        //printing id to SO
        String id = ((Node) e.getSource()).getId();
        System.out.println("Pressed: " + id);

        //don't want to chain 0s without a point
        switch (id) {
            case "zeroButton" ->  {
                if (onLabel.startsWith("0") && (!onLabel.contains("."))) {
                    System.err.println("can't append 0");
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

        //update onLabel, check it's not too big for the label, set it on the label
        onLabel = builder.toString();
        if (isTooBig(onLabel.length())) {
            System.err.println("too big!");
            onLabel = onLabel.substring(0, maxDigits);
        }

        label.setText(onLabel);
    }

    /**
     * stores the number in the label either in count or temp
     * performs an operation if it was stored in memory
     * stores the operation to be performed next.
     * Currently, performed operations:
     * Addition,
     * Subtraction,
     * Multiplication,
     * Division,
     * To The Power Of N,
     * Radical N,
     * Log Base N
     */
    @FXML
    protected void onOperationPressed(ActionEvent e) {
        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            System.err.println("resetting");
            setDefaults();
            return;
        }

        //printing id to SO
        String id = ((Node) e.getSource()).getId();
        System.out.println("Pressed: " + id);

        //if label is empty there is no number to work with
        if (label.getText().equals(""))
            return;

        //count is used to store either the first number ever input or the result of the operation that are going to
        //be chained in the future, it holds the "count"
        //temp is used to a temporary number used to perform the operations with
        if (!hasCount) {
            count = new BigDecimal(label.getText());
            hasCount = true;
        } else {
            temp = new BigDecimal(label.getText());
            hasTemp = true;
        }

        //resets the builder because the text on the label will change due to the operation
        builder = new StringBuilder();

        //perform operations for which you need a temporary value an operation indeed, then resets temp in order to receive the next number
        if (hasTemp && operation != 0) {
            switch (operation) {
                case '+' -> count = addition(count, temp);
                case '-' -> count = subtraction(count, temp);
                case '*' -> count = multiplication(count, temp);
                case '/' ->  {
                    if (temp.doubleValue() == 0.0) {
                        System.err.println("can't divide by 0");
                        onLabel = errorString;
                        label.setText(onLabel);
                        return;
                    }
                    count = division(count, temp);
                }
                case 'n' ->  {
                    if (count.doubleValue() <= 0) {
                        System.err.println("can't extract power");
                        onLabel = errorString;
                        label.setText(onLabel);
                        return;
                    }
                    count = exponentiation(count, temp);
                }
                case 'm' -> {
                    if (temp.doubleValue() == 0.0 || !String.valueOf(temp.doubleValue()).endsWith(".0") || count.doubleValue() < 0.0) {
                        System.err.println("can't extract root");
                        onLabel = errorString;
                        label.setText(onLabel);
                        return;
                    }
                    count = rootExtraction(count, temp);
                }
                case 'l' -> {
                    if (temp.doubleValue() == 1.0 || temp.doubleValue() <= 0.0 || count.doubleValue() < 0.0) {
                        System.err.println("can't perform this log");
                        onLabel = errorString;
                        label.setText(onLabel);
                        return;
                    }
                    count = logBaseN(count, temp);
                }
            }
            hasTemp = false;
            temp = new BigDecimal("0");
        } else {
            System.err.println("no temp in memory");
        }

        //update onLabel, check it's not too big, display it, update builder
        onLabel = count.toString();
        if (isTooBig(onLabel.length())) {
            System.err.println("too big");
            displayErr();
            return;
        }
        label.setText(onLabel);

        //update operation
        switch (id) {
            case "plusButton" -> operation = '+';
            case "minusButton" -> operation = '-';
            case "timesButton" -> operation = '*';
            case "divideButton" -> operation = '/';
            case "toThePowerOfNButton" -> operation = 'n';
            case "radicalNButton" -> operation = 'm';
            case "logBaseNButton" -> operation = 'l';
        }
    }

    /**
     * performs an operation if it was stored in memory
     * and displays the final value of the expression evaluated so far
     */
    @FXML
    protected void onEqualsPressed(ActionEvent e) {
        //printing id to SO
        String id = ((Node) e.getSource()).getId();
        System.out.println("Pressed: " + id);

        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            System.err.println("resetting");
            setDefaults();
            return;
        }

        //check if there is still an operation left to do in memory
        //if there is, perform it and therefore reset temp but also the operation itself because
        //a cycle of operations ends when you press equals of course
        //if there is not just return meaning this method does nothing
        if (operation != 0) {
            if (label.getText().equals(""))
                return;

            temp = new BigDecimal(label.getText());
            switch (operation) {
                case '+' -> count = addition(count, temp);
                case '-' -> count = subtraction(count, temp);
                case '*' -> count = multiplication(count, temp);
                case '/' ->  {
                    if (temp.doubleValue() == 0.0) {
                        System.err.println("can't divide by 0");
                        onLabel = errorString;
                        label.setText(onLabel);
                        return;
                    }
                    count = division(count, temp);
                }
                case 'n' ->  {
                    if (count.doubleValue() <= 0) {
                        System.err.println("can't extract power");
                        onLabel = errorString;
                        label.setText(onLabel);
                        return;
                    }
                    count = exponentiation(count, temp);
                }
                case 'm' -> {
                    if (temp.doubleValue() == 0.0 || !String.valueOf(temp.doubleValue()).endsWith(".0") || count.doubleValue() < 0.0) {
                        System.err.println("can't extract root");
                        onLabel = errorString;
                        label.setText(onLabel);
                        return;
                    }
                    count = rootExtraction(count, temp);
                }
                case 'l' -> {
                    if (temp.doubleValue() == 1.0 || temp.doubleValue() <= 0.0 || count.doubleValue() < 0.0) {
                        System.err.println("can't perform this log");
                        onLabel = errorString;
                        label.setText(onLabel);
                        return;
                    }
                    count = logBaseN(count, temp);
                }
            }
            operation = 0;
            temp = new BigDecimal("0");
            hasTemp = false;
        } else {
            return;
        }

        //reset the builder because the text on the label has changed due to the operation
        builder = new StringBuilder();

        //update onLabel, check it's not too big, update builder
        onLabel = String.valueOf(count);
        if (isTooBig(onLabel.length())) {
            System.out.println("too big");
            displayErr();
            return;
        }
        label.setText(onLabel);
        builder.append(onLabel);

        //reset count for a new cycle of operations to start over
        count = new BigDecimal("0");
        hasCount = false;
    }

    /**
     * appends a point to the label
     */
    @FXML
    protected void onPointPressed(ActionEvent e) {
        //printing id to SO
        String id = ((Node) e.getSource()).getId();
        System.out.println("Pressed: " + id);

        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            System.err.println("resetting");
            setDefaults();
            return;
        }

        //cannot add more than one point
        if (onLabel.contains("."))
            return;

        //reset builder, append onLabel to builder, add the point, set onLabel to label again
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
    protected void onDelPressed(ActionEvent e) {
        //printing id to SO
        String id = ((Node) e.getSource()).getId();
        System.out.println("Pressed: " + id);

        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            System.err.println("resetting");
            setDefaults();
            return;
        }

        //0 is considered the default value of the label, so it may not be erased
        if (onLabel.equals("0"))
            return;

        //reset builder, remove one character from onLabel, re-append onLabel to builder, set onLabel again
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
    protected void onAcPressed(ActionEvent e) {
        //printing id to SO
        String id = ((Node) e.getSource()).getId();
        System.out.println("Pressed: " + id);

        setDefaults();
    }

    /**
     * changes the sign of the number in label
     */
    @FXML
    protected void onPlusMinusPressed(ActionEvent e) {
        //printing id to SO
        String id = ((Node) e.getSource()).getId();
        System.out.println("Pressed: " + id);

        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            System.err.println("resetting");
            setDefaults();
            return;
        }

        //can't change sign of 0
        if (onLabel.equals("0"))
            return;

        //multiply the value on label by -1 to change its sign and strip trailing 0s from it
        BigDecimal value = new BigDecimal(onLabel);
        value = value.multiply(new BigDecimal("-1"));
        value = value.stripTrailingZeros();

        //checking it's not too big, update builder, display it
        onLabel = String.valueOf(value);
        builder = new StringBuilder();
        builder.append(onLabel);
        if (isTooBig(onLabel.length())) {
            System.err.println("too big");
            displayErr();
            return;
        }

        label.setText(onLabel);
    }

    /**
     * switches to a different scene where more
     * complex functions are present
     */
    @FXML
    protected void onFnPressed(ActionEvent e) throws IOException {
        //printing id to SO
        String id = ((Node) e.getSource()).getId();
        System.out.println("Pressed: " + id);

        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            System.err.println("resetting");
            setDefaults();
            return;
        }

        //define fxmlLoader and get text;
        FXMLLoader fxmlLoader;
        onLabel = label.getText();

        //toggle structure with a boolean to load the other fxml file when one is open
        if (isOnSimple) {
            isOnSimple = false;
            fxmlLoader = new FXMLLoader(CalculatorApplication.class.getResource("calculatorComplex.fxml"));
        } else {
            isOnSimple = true;
            fxmlLoader = new FXMLLoader(CalculatorApplication.class.getResource("calculatorSimple.fxml"));
        }

        //apply configuration
        Stage stage = (Stage) ((Node) e.getSource()).getScene().getWindow();
        Scene scene = new Scene(fxmlLoader.load());
        DarculaFX.applyDarculaStyle(scene);
        CalculatorController controller = fxmlLoader.getController();
        //injecting the label's text through the controller each time you switch scene because it cannot be made static
        controller.label.setText(onLabel);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * more operation performed singularly on the value being displayed already
     * without the need to access either count or temp because you get the result immediately
     * without pressing equal, like an instant change, so just change the number according to the operation.
     * Currently, performed operations:
     * To The Power Of Two,
     * Square root,
     * Percentage,
     * Log,
     * ln,
     */
    @FXML
    protected void onMoreOperationPressed(ActionEvent e) {
        //error checking
        onLabel = label.getText();
        if (onLabel.equals("err")) {
            System.err.println("resetting");
            setDefaults();
            return;
        }

        //printing id to SO
        String id = ((Node) e.getSource()).getId();
        System.out.println("Pressed: " + id);

        //get the value on label and perform relative operation
        BigDecimal value = new BigDecimal(onLabel);
        switch (id) {
            case "toThePowerOfTwoButton" -> value = value.pow(2);
            case "radicalTwoButton" ->  {
                if (value.doubleValue() < 0.0) {
                    System.err.println("can't extract square root of a negative number");
                    displayErr();
                    return;
                }
                value = value.sqrt(new MathContext(15));
            }
            case "percentButton" ->  {
                if (value.doubleValue() < 0.0) {
                    System.err.println("can't calculate percentage off a negative number");
                    displayErr();
                    return;
                }
                value = value.divide(new BigDecimal("100"), RoundingMode.HALF_UP);  //divide by 100
            }
            case "logButton" ->  {
                if (value.doubleValue() <= 0.0 || value.doubleValue() == 1.0) {
                    System.err.println("can't log a negative number or 0 or 1");
                    displayErr();
                    return;
                }
                value = BigDecimal.valueOf(Math.log10(value.doubleValue()));
            }
            case "lnButton" ->  {
                if (value.doubleValue() <= 0.0 || value.doubleValue() == 1.0) {
                    System.err.println("can't ln a negative number or 0 or 1");
                    displayErr();
                    return;
                }
                value = BigDecimal.valueOf(Math.log(value.doubleValue()));
            }
        }

        //round if decimals are present
        if (value.toString().contains(".")) {
            value = round(value);
        }

        //update onLabel, check it's not too big, clear builder, append onLabel, display it
        onLabel = String.valueOf(value);
        if (isTooBig(onLabel.length())) {
            System.err.println("too big");
            displayErr();
            return;
        }

        builder = new StringBuilder();
        builder.append(onLabel);
        label.setText(onLabel);
    }

    /**
     * Resets all the variables of the program to default values
     * meaning it's like running again
     */
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
     * Rounds the number depending on the max digits that are possible on the label
     * in order to show the max precision
     *
     * @throws IllegalArgumentException if it doesn't have a point
     */
    private BigDecimal round(BigDecimal d) {
        if (!d.toString().contains("."))
            throw new IllegalArgumentException();
        int leftDigits = d.toString().substring(0, d.toString().indexOf('.')).length();
        d = d.setScale(maxDigits - leftDigits - 2, RoundingMode.HALF_UP);
        d = d.stripTrailingZeros();
        return d;
    }

    /**
     * Displays err on the label which is recognized as error condition
     * When this condition is met all the other methods will call setDefaults()
     * and return
     */
    private void displayErr() {
        onLabel = "err";
        label.setText(onLabel);
    }

    /**
     * checks if the number is bigger than the max digits possible on the label
     */
    private boolean isTooBig(int i) {
        return i > maxDigits;
    }

    /**
     * updates a with the sum of a and b
     */
    private BigDecimal addition(BigDecimal a, BigDecimal b) {
        a = a.add(b);
        a = a.stripTrailingZeros();
        System.out.println("result: " + a);
        return a;
    }

    /**
     * updates a with the difference of a and b
     */
    private BigDecimal subtraction(BigDecimal a, BigDecimal b) {
        a = a.subtract(b);
        a = a.stripTrailingZeros();
        System.out.println("result: " + a);
        return a;
    }

    /**
     * updates a with the product of a and b
     */
    private BigDecimal multiplication(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = a.multiply(b);
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("result: " + a);
        return a;
    }

    /**
     * updates a with the quotient of a and b
     */
    private BigDecimal division(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = a.divide(b, RoundingMode.HALF_UP);
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("result: " + a);
        return a;
    }

    /**
     * updates a with the power of a and b
     */
    private BigDecimal exponentiation(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = BigDecimal.valueOf(Math.pow(a.doubleValue(), b.doubleValue()));
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("result: " + a);
        return a;
    }

    /**
     * updates a with the bth root of a
     */
    private BigDecimal rootExtraction(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = BigDecimal.valueOf(Math.pow(a.doubleValue(), 1 / b.doubleValue()));
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("result: " + a);
        return a;
    }

    /**
     * updates a with the value of log base b of a
     */
    private BigDecimal logBaseN(BigDecimal a, BigDecimal b) {
        a = a.setScale(15, RoundingMode.HALF_UP);
        a = BigDecimal.valueOf(Math.log(a.doubleValue()) / Math.log(b.doubleValue()));
        if (a.toString().contains("."))
            a = round(a);
        System.out.println("result: " + a);
        return a;
    }

}