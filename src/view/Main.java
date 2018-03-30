package view;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import model.Parser;

public class Main extends Application {

    private Label display1 = new Label("");
    private Label display2 = new Label("0");
    private boolean displayIsClean = true;
    private Button [] buttons = new Button[20];
    private int i = 0;
    private Parser parser = new Parser("0");

    private final String INPUTS = "()1234567890.+-"+Character.toString((char)0x00F7)+Character.toString((char)0x00D7);
    private final String BACKSPACE = Character.toString((char)0x232B);
    private final String MULTIPLY  = Character.toString((char)0x00D7);
    private final String DIVIDE    = Character.toString((char)0x00F7);

    @Override
    public void start(Stage primaryStage) throws Exception{
        primaryStage.setTitle("calculator");

        /* gridPane settings */
        GridPane gridPane = new GridPane();
        gridPane.setHgap(8); // width of gaps between columns
        gridPane.setVgap(8); // width of gaps between rows
        gridPane.setAlignment(Pos.CENTER); // The alignment of of the grid within gridPane's width and height

        /* gridPane layout */
        String font = "-fx-font: 22 arial;";

        display1.setMinSize(224,50);
        display1.setStyle(font + "-fx-border-width: 1;" + "-fx-border-color: grey;" + "-fx-border-radius: 5;");
        gridPane.add(display1, 0, 0, 4, 1);

        display2.setMinSize(224,50);
        display2.setStyle("-fx-font: 18 arial;" + "-fx-border-width: 1;" + "-fx-border-color: grey;" + "-fx-border-radius: 5;");
        gridPane.add(display2, 0, 1, 4, 1);


        String [] buttonText = new String[]{
                "(", ")", BACKSPACE,"C",
                "7","8","9","+",
                "4","5","6","-",
                "1","2","3",MULTIPLY,
                "0",".","=",DIVIDE
        };
        for (i = 0; i < 20; i++) {
            buttons[i] = new Button(buttonText[i]);
            buttons[i].setStyle(font);
            buttons[i].setMinSize(50, 50);
            gridPane.add(buttons[i], (i % 4), (i / 4 + 2));

            buttons[i].setOnAction(e -> buttonIsPressed((Button)e.getSource()));
        }


        Scene scene = new Scene(gridPane, 256, 430);
        primaryStage.setScene(scene);
        primaryStage.show();

        // add keyboard support
        scene.setOnKeyTyped(event -> { // keyboard event
            if ((INPUTS+BACKSPACE+MULTIPLY+DIVIDE+"C=").contains(event.getCharacter()))
                receiveInput(event.getCharacter());
        });
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void buttonIsPressed(Button btn) {
        String symbol = btn.getText();
        receiveInput(symbol);
    }

    public void receiveInput(String symbol) {
        String oldText = display2.getText();
        if (INPUTS.contains(symbol)) {
            if (displayIsClean) {
                display2.setText(symbol);
                displayIsClean = false;
            }
            else {
                if (symbol.equals(MULTIPLY)) symbol = "*";
                else if (symbol.equals(DIVIDE)) symbol = "/";
                display2.setText(oldText + symbol);
            }
        }
        else if (symbol.equals("C")) {
            display2.setText("0");
            displayIsClean = true;
        }
        else if (symbol.equals(BACKSPACE)) {
            if (oldText.length() == 1) {
                display2.setText("0");
                displayIsClean = true;
            }
            else if (!oldText.isEmpty())
                display2.setText(oldText.substring(0, oldText.length()-1));
        }
        else {// it's "="
            try {
                display1.setText(Double.toString(new Parser(oldText).parseToTree().evaluate()));
                displayIsClean = true;
            } catch (IllegalArgumentException e ) {
                display1.setText("Syntax Error");
            }
        }
    }
}
