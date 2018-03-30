package view;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;

public class InputBtn extends Button implements EventHandler<ActionEvent>{
    private static String displayText = "";
    private static boolean clean = true;

    public InputBtn(String text) {
        super(text);
    }

    @Override
    public void handle(ActionEvent e) {
        displayText = clean ? this.getText() : displayText + this.getText();
    }
}
