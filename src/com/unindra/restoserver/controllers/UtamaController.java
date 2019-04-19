package com.unindra.restoserver.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
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
import static com.unindra.restoserver.models.Transaksi.getTransaksiList;
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
                                setText(item + " lv." + i.getLevel_item());
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
                                            alert.getDialog().hide();
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

        TreeItem<Item> rootItem = new RecursiveTreeItem<>(filteredList, RecursiveTreeObject::getChildren);

        pesananTableView.setRoot(rootItem);
        pesananTableView.getColumns().add(mejaCol);
        pesananTableView.getColumns().add(namaCol);
        pesananTableView.getColumns().add(jumlahCol);
        pesananTableView.getColumns().add(terimaCol);
        pesananTableView.getColumns().add(tolakCol);
        pesananTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        TreeTableColumn<Transaksi, String> mejaTransaksiCol = new TreeTableColumn<>("No Meja");
        TreeTableColumn<Transaksi, Integer> totalCol = new TreeTableColumn<>("Total Harga");
        TreeTableColumn<Transaksi, String> billCol = new TreeTableColumn<>("Bill");
        TreeTableColumn<Transaksi, String> strukCol = new TreeTableColumn<>("Struk");
        TreeTableColumn<Transaksi, String> simpanCol = new TreeTableColumn<>("Simpan");

        mejaTransaksiCol.setCellValueFactory(param -> param.getValue().getValue().no_mejaProperty());
        totalCol.setCellValueFactory(param -> param.getValue().getValue().totalProperty());
        billCol.setCellValueFactory(param -> new SimpleStringProperty(""));
        strukCol.setCellValueFactory(param -> new SimpleStringProperty(""));
        simpanCol.setCellValueFactory(param -> new SimpleStringProperty(""));

        billCol.setCellFactory(new Callback<TreeTableColumn<Transaksi, String>, TreeTableCell<Transaksi, String>>() {
            @Override
            public TreeTableCell<Transaksi, String> call(TreeTableColumn<Transaksi, String> param) {
                return new TreeTableCell<Transaksi, String>() {
                    final JFXButton button = new JFXButton("Cetak");
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            button.setStyle("-fx-background-color: #EAEAEA");
                            button.setOnAction(event -> {

                            });
                            setGraphic(button);
                            setText(null);
                        }
                    }
                };
            }
        });

        strukCol.setCellFactory(new Callback<TreeTableColumn<Transaksi, String>, TreeTableCell<Transaksi, String>>() {
            @Override
            public TreeTableCell<Transaksi, String> call(TreeTableColumn<Transaksi, String> param) {
                return new TreeTableCell<Transaksi, String>() {
                    final JFXButton button = new JFXButton("Cetak");
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            button.setStyle("-fx-background-color: #EAEAEA");
                            button.setOnAction(event -> {
                                Dialog jumlahTunaiDialog = new Dialog((Stage) pesananTableView.getScene().getWindow());
                                JFXTextField tunaiField = new JFXTextField();
                                tunaiField.textProperty().addListener((observable, oldValue, newValue) -> {
                                    if (!newValue.matches("\\d*")) {
                                        tunaiField.setText(newValue.replaceAll("[^\\d]", ""));
                                    }
                                });

                                jumlahTunaiDialog.input(
                                        tunaiField,
                                        e -> {
                                            System.out.println(tunaiField.getText());
                                            jumlahTunaiDialog.getDialog().hide();
                                        });
                            });
                            setGraphic(button);
                            setText(null);
                        }
                    }
                };
            }
        });

        simpanCol.setCellFactory(new Callback<TreeTableColumn<Transaksi, String>, TreeTableCell<Transaksi, String>>() {
            @Override
            public TreeTableCell<Transaksi, String> call(TreeTableColumn<Transaksi, String> param) {
                return new TreeTableCell<Transaksi, String>() {
                    final JFXButton button = new JFXButton("Simpan");
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            button.setStyle("-fx-background-color: #EAEAEA");
                            button.setOnAction(event -> {
                                Dialog confirmDialog = new Dialog((Stage) pesananTableView.getScene().getWindow());
                                confirmDialog.confirmation(
                                        "Transaksi sudah selesai?",
                                        e -> {
                                            Transaksi transaksi = getTransaksiList().get(getIndex());
                                            transaksi.simpan();
                                            confirmDialog.getDialog().hide();
                                        });

                            });
                            setGraphic(button);
                            setText(null);
                        }
                    }
                };
            }
        });

        TreeItem<Transaksi> rootTrans = new RecursiveTreeItem<>(getTransaksiList(), RecursiveTreeObject::getChildren);
        pembayaranTableView.setRoot(rootTrans);
        pembayaranTableView.getColumns().add(mejaTransaksiCol);
        pembayaranTableView.getColumns().add(totalCol);
        pembayaranTableView.getColumns().add(billCol);
        pembayaranTableView.getColumns().add(strukCol);
        pembayaranTableView.getColumns().add(simpanCol);
        pembayaranTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);
    }
}
