package com.unindra.restoserver.controllers;

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.Dialog;
import com.unindra.restoserver.models.Menu;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

import java.net.URL;
import java.util.ResourceBundle;

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

    private ObservableList<String> tipeList;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeTableColumn<Menu, String> namaCol = new TreeTableColumn<>("Nama");
        TreeTableColumn<Menu, String> tipeCol = new TreeTableColumn<>("Tipe");
        TreeTableColumn<Menu, String> hargaCol = new TreeTableColumn<>("Harga");

        namaCol.setCellValueFactory(param -> param.getValue().getValue().namaProperty());
        tipeCol.setCellValueFactory(param -> param.getValue().getValue().tipeProperty());
        hargaCol.setCellValueFactory(param -> param.getValue().getValue().hargaProperty());

        TreeItem<Menu> root = new RecursiveTreeItem<>(getMenus(), RecursiveTreeObject::getChildren);
        menuTableView.setRoot(root);
        menuTableView.getColumns().add(namaCol);
        menuTableView.getColumns().add(tipeCol);
        menuTableView.getColumns().add(hargaCol);
        menuTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        tipeList = FXCollections.observableArrayList("ramen", "minuman", "cemilan", "lainnya");
        tipeComboBox.setItems(tipeList);

        hargaField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d*")) {
                hargaField.setText(newValue.replaceAll("[^\\d]", ""));
            }
        });

        menuTableView.selectionModelProperty().addListener((observable, oldValue, newValue) -> System.out.println(observable));
    }

    private Dialog getAlert() {
        return new Dialog((Stage) actionButton.getScene().getWindow());
    }

    public void actionHandle() {
        if (actionButton.getText().equals("Tambah")) {
            Menu menu = new Menu(
                    namaField.getText(),
                    tipeComboBox.getSelectionModel().getSelectedItem(),
                    Integer.valueOf(hargaField.getText()),
                    deskArea.getText());
            if (menu.tambah()) {
                getAlert().information("Berhasil!", "Menu berhasil ditambahkan");
                reset();
            }
            else getAlert().information("Gagal", "Menu gagal ditambahkan");
        } else {
            Menu menu = menuTableView.getSelectionModel().getSelectedItem().getValue();
            menu.setNama_menu(namaField.getText());
            menu.setTipe_menu(tipeComboBox.getSelectionModel().getSelectedItem());
            menu.setHarga_menu(Integer.valueOf(hargaField.getText()));
            menu.setDeskripsi(deskArea.getText());
            if (menu.ubah()) {
                getAlert().information("Berhasil!", "Menu berhasil diubah");
                reset();
            } else getAlert().information("Gagal", "Menu gagal diubah");
        }
    }

    public void pilihHandle(MouseEvent mouseEvent) {
        if (!menuTableView.getSelectionModel().isEmpty()) {
            Menu menu = menuTableView.getSelectionModel().getSelectedItem().getValue();
            namaField.setText(menu.getNama_menu());
            int index = tipeList.indexOf(menu.getTipe_menu());
            tipeComboBox.getSelectionModel().clearAndSelect(index);
            hargaField.setText(String.valueOf(menu.getHarga_menu()));
            deskArea.setText(menu.getDeskripsi());
            titleLabel.setText("UBAH MENU");
            hapusButton.setVisible(true);
            actionButton.setText("Ubah");
            actionButton.getStyleClass().set(2, "ubah");
        }
        if (mouseEvent.getClickCount() == 2) reset();
    }

    public void hapusHandle() {
        Dialog alert = getAlert();
        alert.confirmation(
                "Anda yakin ingin menghapus menu ini?",
                event -> {
                    Menu menu = menuTableView.getSelectionModel().getSelectedItem().getValue();
                    if (menu.hapus()) {
                        alert.information("Berhasil!", "Menu berhasil dihapus");
                        reset();
                    } else alert.information("Gagal", "Menu gagal dihapus");
                });
    }

    private void reset() {
        titleLabel.setText("TAMBAH MENU");
        hapusButton.setVisible(false);
        actionButton.setText("Tambah");
        actionButton.getStyleClass().set(2, "tambah");
        menuTableView.getSelectionModel().clearSelection();
        namaField.setText("");
        tipeComboBox.getSelectionModel().clearSelection();
        hargaField.setText("");
        deskArea.setText("");
    }

}
