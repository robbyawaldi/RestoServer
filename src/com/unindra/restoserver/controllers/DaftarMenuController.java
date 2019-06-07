package com.unindra.restoserver.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.Dialog;
import com.unindra.restoserver.models.DetailRamen;
import com.unindra.restoserver.models.Level;
import com.unindra.restoserver.models.Menu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.ResourceBundle;

import static com.unindra.restoserver.models.Level.getLevels;
import static com.unindra.restoserver.models.Menu.getMenus;

public class DaftarMenuController implements Initializable {
    public JFXTreeTableView<Menu> menuTableView;
    public JFXButton actionButton;
    public JFXButton hapusButton;
    public JFXTextField namaField;
    public JFXTextField hargaField;
    public JFXComboBox<String> tipeComboBox;
    public JFXTextArea deskArea;
    public Label titleLabel;
    public JFXTreeTableView<Level> levelTableView;
    public JFXTextField hargaLevelField;
    public JFXTextField levelField;
    public HBox formForRamenPane;
    public JFXButton pilihGambarButton;

    private ObservableList<String> tipeList;
    private Menu menu;
    private Level level;
    private DetailRamen detailRamen;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeTableColumn<Menu, String> namaCol = new TreeTableColumn<>("Nama");
        TreeTableColumn<Menu, String> tipeCol = new TreeTableColumn<>("Tipe");
        TreeTableColumn<Menu, String> hargaCol = new TreeTableColumn<>("Harga");

        namaCol.setCellValueFactory(param -> param.getValue().getValue().nama_menuProperty());
        tipeCol.setCellValueFactory(param -> param.getValue().getValue().tipeProperty());
        hargaCol.setCellValueFactory(param -> param.getValue().getValue().harga_menuProperty());

        TreeItem<Menu> rootMenu = new RecursiveTreeItem<>(getMenus(), RecursiveTreeObject::getChildren);
        menuTableView.setRoot(rootMenu);
        menuTableView.getColumns().add(namaCol);
        menuTableView.getColumns().add(tipeCol);
        menuTableView.getColumns().add(hargaCol);
        menuTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        tipeList = FXCollections.observableArrayList("ramen","minuman", "cemilan", "lainnya");
        tipeComboBox.setItems(tipeList);

        TreeTableColumn<Level, Integer> levelCol = new TreeTableColumn<>("Level");
        TreeTableColumn<Level, String> hargaLevelCol = new TreeTableColumn<>("Harga");

        levelCol.setCellValueFactory(param -> param.getValue().getValue().levelProperty());
        hargaLevelCol.setCellValueFactory(param -> param.getValue().getValue().harga_levelProperty());

        TreeItem<Level> rootLevel = new RecursiveTreeItem<>(getLevels(), RecursiveTreeObject::getChildren);
        levelTableView.setRoot(rootLevel);
        levelTableView.getColumns().add(levelCol);
        levelTableView.getColumns().add(hargaLevelCol);
        levelTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        hargaField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                hargaField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        hargaLevelField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                hargaLevelField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });
    }

    public void actionHandle() {
        if (actionButton.getText().equals("Tambah")) {
            menu = new Menu(
                    namaField.getText(),
                    tipeComboBox.getSelectionModel().getSelectedItem(),
                    Integer.valueOf(hargaField.getText()));
            if (menu.getTipe().equals("ramen")) {
                if (detailRamen != null) {
                    detailRamen.setNama_menu(namaField.getText());
                    detailRamen.setDeskripsi(deskArea.getText());
                    if (menu.add() && detailRamen.add()) {
                        getDialog().information("Berhasil!", "Menu berhasil ditambahkan");
                        reset();
                    } else getDialog().information("Gagal", "Menu gagal ditambahkan");
                } else getDialog().information("Gagal", "Gambar belum dipilih");
            } else {
                if (menu.add()) {
                    getDialog().information("Berhasil!", "Menu berhasil ditambahkan");
                    reset();
                } else getDialog().information("Gagal", "Menu gagal ditambahkan");
            }
        } else { // Ubah
            menu.setNama_menu(namaField.getText());
            menu.setTipe(tipeComboBox.getSelectionModel().getSelectedItem());
            menu.setHarga_menu(Integer.valueOf(hargaField.getText()));
            if (menu.getTipe().equals("ramen")) {
                detailRamen.setNama_menu(menu.getNama_menu());
                detailRamen.setDeskripsi(deskArea.getText());
                if (menu.update() && detailRamen.update()) {
                    getDialog().information("Berhasil!", "Menu berhasil diubah");
                    reset();
                } else getDialog().information("Gagal", "Menu gagal diubah");
            } else {
                if (menu.update()) {
                    getDialog().information("Berhasil!", "Menu berhasil diubah");
                    reset();
                } else getDialog().information("Gagal", "Menu gagal diubah");
            }
        }
    }

    public void ubahLevelHandle() {
        level.setHarga_level(Integer.parseInt(hargaLevelField.getText()));
        if (level.update()) {
            getDialog().information("Berhasil!", "Level berhasil diubah");
            reset();
        }
    }

    public void pilihHandle(MouseEvent mouseEvent) {
        if (!menuTableView.getSelectionModel().isEmpty()) {
            menu = menuTableView.getSelectionModel().getSelectedItem().getValue();
            detailRamen = DetailRamen.detailRamen(menu);

            namaField.setText(menu.getNama_menu());
            int index = tipeList.indexOf(menu.getTipe());
            tipeComboBox.getSelectionModel().clearAndSelect(index);
            hargaField.setText(String.valueOf(menu.getHarga_menu()));
            if (detailRamen != null) deskArea.setText(detailRamen.getDeskripsi());

            titleLabel.setText("UBAH MENU");
            namaField.setDisable(true);
            hapusButton.setVisible(true);
            actionButton.setText("Ubah");
            actionButton.getStyleClass().set(2, "ubah");
        }
        if (mouseEvent.getClickCount() == 2) reset();
    }

    public void pilihLevelHandle(MouseEvent mouseEvent) {
        if (!levelTableView.getSelectionModel().isEmpty()) {
            level = levelTableView.getSelectionModel().getSelectedItem().getValue();
            levelField.setText(String.valueOf(level.getLevel()));
            hargaLevelField.setText(String.valueOf(level.getHarga_level()));
            hargaLevelField.setDisable(false);
        }
        if (mouseEvent.getClickCount() == 2) reset();
    }

    public void hapusHandle() {
        Dialog alert = getDialog();
        alert.confirmation(
                "Anda yakin ingin menghapus menu ini?",
                event -> {
                    Menu menu = menuTableView.getSelectionModel().getSelectedItem().getValue();
                    if (menu.getTipe().equals("ramen")) {
                        if (menu.delete() && detailRamen.delete()) {
                            alert.information("Berhasil!", "Menu berhasil dihapus");
                            reset();
                        } else alert.information("Gagal", "Menu gagal dihapus");
                    } else {
                        if (menu.delete()) {
                            alert.information("Berhasil!", "Menu berhasil dihapus");
                            reset();
                        } else alert.information("Gagal", "Menu gagal dihapus");
                    }
                });
    }

    private void reset() {
        titleLabel.setText("TAMBAH MENU");
        hapusButton.setVisible(false);
        actionButton.setText("Tambah");
        actionButton.getStyleClass().set(2, "tambah");
        menuTableView.getSelectionModel().clearSelection();
        namaField.setDisable(false);
        namaField.setText("");
        tipeComboBox.getSelectionModel().clearSelection();
        hargaField.setText("");
        deskArea.setText("");
        levelField.setText("");
        hargaLevelField.setText("");
        hargaLevelField.setDisable(true);
        levelTableView.getSelectionModel().clearSelection();
        namaField.requestFocus();
        pilihGambarButton.setText("Pilih gambar... (max : 2048 KB)");
    }

    public void tipeHandle() {
        if (tipeComboBox.getSelectionModel().getSelectedItem() != null) {
            if (tipeComboBox.getSelectionModel().getSelectedItem().equals("ramen")) formForRamenPane.setDisable(false);
            else {
                formForRamenPane.setDisable(true);
                detailRamen = null;
                deskArea.setText("");
                pilihGambarButton.setText("Pilih gambar... (max : 2048 KB)");
            }
        }
    }

    public void pilihGambarHandle() throws IOException {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open Resource File");
        File file = fileChooser.showOpenDialog(actionButton.getScene().getWindow());
        if (file != null) {
            if (file.length() <= 2048000) {
                pilihGambarButton.setText(file.getName());
                detailRamen = new DetailRamen(Files.readAllBytes(file.toPath()));
            } else {
                getDialog().information("Gagal", "Ukuran foto terlalu besar");
            }
        }
    }

    private Dialog getDialog() {
        return new Dialog((Stage) actionButton.getScene().getWindow());
    }
}
