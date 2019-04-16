package com.unindra.restoserver;

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.JFXAlert;
import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialogLayout;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class Dialog {

    private JFXAlert<String> alert;

    public Dialog(Stage stage) {
        alert = new JFXAlert<>(stage);
        alert.setOverlayClose(false);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.setAnimation(JFXAlertAnimation.TOP_ANIMATION);
    }

    public static JFXDialogLayout getDialogLayout(Node heading, Node body, JFXButton... buttons) {
        JFXDialogLayout dialogLayout = new JFXDialogLayout();
        dialogLayout.setActions(buttons);
        dialogLayout.setHeading(heading);
        dialogLayout.setBody(body);
        return dialogLayout;
    }

    public JFXAlert<String> getAlert() {
        return alert;
    }

    public void information(String header, String body) {
        JFXButton okButton = new JFXButton("Ok");
        okButton.setOnAction(event -> alert.hide());
        alert.setContent(getDialogLayout(
                new Label(header),
                new Label(body),
                okButton
        ));
        alert.show();
    }

    public void confirmation(String body, EventHandler<ActionEvent> eventConfirm) {
        JFXButton yaButton = new JFXButton("Ya");
        JFXButton batalButton = new JFXButton("Batal");
        yaButton.setOnAction(eventConfirm);
        batalButton.setOnAction(event -> alert.hide());
        alert.setContent(getDialogLayout(
                new Label("Konfirmasi"),
                new Label(body),
                yaButton,
                batalButton
        ));
        alert.show();
    }
}
