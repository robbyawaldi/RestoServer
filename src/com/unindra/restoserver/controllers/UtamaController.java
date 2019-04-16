package com.unindra.restoserver.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.Dialog;
import com.unindra.restoserver.models.Item;
import com.unindra.restoserver.models.Transaksi;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.Initializable;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import static com.unindra.restoserver.models.ItemService.*;
import static com.unindra.restoserver.models.Menu.menu;
import static java.util.Objects.requireNonNull;

public class UtamaController implements Initializable {

    public JFXTreeTableView<Item> pesananTableView;
    public JFXTreeTableView<Transaksi> pembayaranTableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeTableColumn<Item, String> mejaCol = new TreeTableColumn<>("No Meja");
        TreeTableColumn<Item, String> namaCol = new TreeTableColumn<>("Nama");
        TreeTableColumn<Item, Integer> jumlahCol = new TreeTableColumn<>("Jumlah");
        TreeTableColumn<Item, String> terimaCol = new TreeTableColumn<>("Terima");
        TreeTableColumn<Item, String> tolakCol = new TreeTableColumn<>("Tolak");

        mejaCol.setCellValueFactory(param -> param.getValue().getValue().no_mejaProperty());
        namaCol.setCellValueFactory(param -> menu(param.getValue().getValue()).namaProperty());
        jumlahCol.setCellValueFactory(param -> param.getValue().getValue().jumlahProperty());
        terimaCol.setCellValueFactory(param -> new SimpleStringProperty(""));
        tolakCol.setCellValueFactory(param -> new SimpleStringProperty(""));

        namaCol.setCellFactory(new Callback<TreeTableColumn<Item, String>, TreeTableCell<Item, String>>() {
            @Override
            public TreeTableCell<Item, String> call(TreeTableColumn<Item, String> param) {
                return new TreeTableCell<Item, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setText(null);
                        } else {
                            Item i = getItems().get(getIndex());
                            if (requireNonNull(menu(i)).getTipe_menu().equals("ramen"))
                                setText(item + " lv." + i.getLvl_item());
                            else setText(item);
                        }
                    }
                };
            }
        });

        terimaCol.setCellFactory(new Callback<TreeTableColumn<Item, String>, TreeTableCell<Item, String>>() {
            @Override
            public TreeTableCell<Item, String> call(TreeTableColumn<Item, String> param) {
                return new TreeTableCell<Item, String>() {
                    final JFXButton button = new JFXButton("Terima");

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            button.setStyle("-fx-background-color: #EAEAEA");
                            button.setOnAction(event -> {
                                Item i = pesananTableView.getRoot().getChildren().get(getIndex()).getValue();
                                i.terima();
                                update(i);
                            });
                            setGraphic(button);
                            setText(null);
                        }
                    }
                };
            }
        });

        tolakCol.setCellFactory(new Callback<TreeTableColumn<Item, String>, TreeTableCell<Item, String>>() {
            @Override
            public TreeTableCell<Item, String> call(TreeTableColumn<Item, String> param) {
                return new TreeTableCell<Item, String>() {
                    final JFXButton button = new JFXButton("Tolak");

                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            button.setStyle("-fx-background-color: #EAEAEA");
                            button.setOnAction(event -> {
                                Dialog alert = new Dialog((Stage) pesananTableView.getScene().getWindow());
                                Item i = pesananTableView.getRoot().getChildren().get(getIndex()).getValue();

                                alert.confirmation(
                                        "Anda yakin ingin menolak pesanan ini?",
                                        e -> {
                                            delete(i);
                                            alert.getAlert().hide();
                                        });
                            });
                            setGraphic(button);
                            setText(null);
                        }
                    }
                };
            }
        });

        Predicate<Item> predicate = item -> item.getStatus_item().equals("dipesan");
        FilteredList<Item> filteredList = new FilteredList<>(getItems(), predicate);
        getItems().addListener((ListChangeListener<Item>) c -> filteredList.setPredicate(predicate));

        TreeItem<Item> root = new RecursiveTreeItem<>(filteredList, RecursiveTreeObject::getChildren);

        pesananTableView.setRoot(root);
        pesananTableView.getColumns().add(mejaCol);
        pesananTableView.getColumns().add(namaCol);
        pesananTableView.getColumns().add(jumlahCol);
        pesananTableView.getColumns().add(terimaCol);
        pesananTableView.getColumns().add(tolakCol);
        pesananTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        TreeTableColumn<Transaksi, String> mejaTransaksiCol = new TreeTableColumn<>("No Meja");
        TreeTableColumn<Transaksi, Integer> totalCol = new TreeTableColumn<>("Total Harga");
        TreeTableColumn<Transaksi, String> billCol = new TreeTableColumn<>("Bill");
        TreeTableColumn<Transaksi, String> bayarCol = new TreeTableColumn<>("Bayar");

        mejaTransaksiCol.setCellValueFactory(param -> param.getValue().getValue().no_mejaProperty());
        totalCol.setCellValueFactory(param -> param.getValue().getValue().totalProperty());

        pembayaranTableView.getColumns().add(mejaTransaksiCol);
        pembayaranTableView.getColumns().add(totalCol);
        pembayaranTableView.getColumns().add(billCol);
        pembayaranTableView.getColumns().add(bayarCol);
        pembayaranTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
    }
}
