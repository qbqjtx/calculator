package sample;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    private Label display = new Label("0");
    private boolean displayIsClean = true;
    private Button [] buttons = new Button[20];
    private int i = 0;
    private final String INPUTS = "()1234567890.+-"+Character.toString((char)0x00F7)+Character.toString((char)0x00D7);

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

        display.setMinSize(224,50);
        display.setStyle(font + "-fx-border-width: 1;" + "-fx-border-color: grey;" + "-fx-border-radius: 5;");
        gridPane.add(display, 0, 0, 4, 1);


        String [] buttonText = new String[]{
                "(", ")",Character.toString((char)0x232B),"C",
                "7","8","9","+",
                "4","5","6","-",
                "1","2","3",Character.toString((char)0x00D7),
                "0",".","=",Character.toString((char)0x00F7)
        };
        for (i = 0; i < 20; i++) {
            buttons[i] = new Button(buttonText[i]);
            buttons[i].setStyle(font);
            buttons[i].setMinSize(50, 50);
            gridPane.add(buttons[i], (i % 4), (i / 4 + 1));

            buttons[i].setOnAction(e -> buttonIsPressed((Button)e.getSource()));
        }


        Scene scene = new Scene(gridPane, 256, 372);
        primaryStage.setScene(scene);
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }

    public void buttonIsPressed(Button btn) {
        String symbol = btn.getText();
        if (INPUTS.contains(symbol)) {
            if (displayIsClean) {
                display.setText(symbol);
                displayIsClean = false;
            }
            else {
                display.setText(display.getText() + symbol);
            }
        }
    }
}
