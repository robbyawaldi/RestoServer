package com.unindra.restoserver.controllers;

import com.jfoenix.controls.JFXPasswordField;
import com.jfoenix.controls.JFXTextField;
import com.unindra.restoserver.Dialog;
import com.unindra.restoserver.models.User;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SignInController {

    public JFXTextField usernameField;
    public JFXPasswordField passwordField;

    public void signInAction() throws IOException {
        User user = User.user(usernameField.getText());
        if (user != null) {
            if (user.getPassword().equals(passwordField.getText())) {
                FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/fxml/app.fxml"));
                Parent parent = fxmlLoader.load();
                ((AppController) fxmlLoader.getController()).setUser(user);
                getStage().setScene(new Scene(parent));
                usernameField.requestFocus();
                usernameField.setText("");
                passwordField.setText("");
            } else {
                getDialog().information("Error", "Password salah");
                passwordField.requestFocus();
            }
        } else {
            getDialog().information("Error", "Username tidak ditemukan");
            usernameField.requestFocus();
        }
    }

    private Stage getStage() {
        return (Stage) usernameField.getScene().getWindow();
    }

    private Dialog getDialog() {
        return new Dialog(getStage());
    }
}
