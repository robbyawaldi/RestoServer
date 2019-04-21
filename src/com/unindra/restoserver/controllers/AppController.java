package com.unindra.restoserver.controllers;

import com.jfoenix.controls.JFXButton;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class AppController implements Initializable {

    public ScrollPane scrollPane;
    public JFXButton utamaButton;
    public JFXButton menuButton;
    public JFXButton laporanButton;
    public JFXButton keluarButton;

    private FlowPane utama;
    private FlowPane daftarmenu;
    private FlowPane laporan;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            utama = (FlowPane) setPane("utama");
            daftarmenu = (FlowPane) setPane("daftarmenu");
            laporan = (FlowPane) setPane("laporan");
            scrollPane.setContent(utama);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Pane setPane(String fxml) throws IOException {
        return FXMLLoader.load(getClass().getResource("/fxml/"+fxml+".fxml"));
    }

    public void menuHandle(ActionEvent actionEvent) {
        utamaButton.getStyleClass().set(2, "halaman-utama");
        menuButton.getStyleClass().set(2, "daftar-menu");
        laporanButton.getStyleClass().set(2, "laporan");

        Object source = actionEvent.getSource();
        if (utamaButton.equals(source)) {
            utamaButton.getStyleClass().set(2, "halaman-utama-pressed");
            scrollPane.setContent(utama);
        }
        else if (menuButton.equals(source)) {
            menuButton.getStyleClass().set(2, "daftar-menu-pressed");
            scrollPane.setContent(daftarmenu);
        } else if (laporanButton.equals(source)) {
            laporanButton.getStyleClass().set(2, "laporan-pressed");
            scrollPane.setContent(laporan);
        }
    }
}
