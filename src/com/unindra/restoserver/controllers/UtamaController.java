package com.unindra.restoserver.controllers;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXTextField;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.Dialog;
import com.unindra.restoserver.Laporan;
import com.unindra.restoserver.models.Pesanan;
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

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.Predicate;

import static com.unindra.restoserver.models.PesananService.*;
import static com.unindra.restoserver.models.Menu.menu;
import static com.unindra.restoserver.models.TransaksiService.getTransaksiList;

public class UtamaController implements Initializable {

    public JFXTreeTableView<Pesanan> pesananTableView;
    public JFXTreeTableView<Transaksi> pembayaranTableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeTableColumn<Pesanan, String> mejaCol = new TreeTableColumn<>("No Meja");
        TreeTableColumn<Pesanan, String> namaCol = new TreeTableColumn<>("Nama");
        TreeTableColumn<Pesanan, Integer> jumlahCol = new TreeTableColumn<>("Jumlah");
        TreeTableColumn<Pesanan, String> terimaCol = new TreeTableColumn<>("Terima");
        TreeTableColumn<Pesanan, String> tolakCol = new TreeTableColumn<>("Tolak");

        mejaCol.setCellValueFactory(param -> param.getValue().getValue().no_mejaProperty());
        namaCol.setCellValueFactory(param -> menu(param.getValue().getValue()).namaProperty());
        jumlahCol.setCellValueFactory(param -> param.getValue().getValue().jumlahProperty());
        terimaCol.setCellValueFactory(param -> new SimpleStringProperty(""));
        tolakCol.setCellValueFactory(param -> new SimpleStringProperty(""));

        namaCol.setCellFactory(new Callback<TreeTableColumn<Pesanan, String>, TreeTableCell<Pesanan, String>>() {
            @Override
            public TreeTableCell<Pesanan, String> call(TreeTableColumn<Pesanan, String> param) {
                return new TreeTableCell<Pesanan, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setText(null);
                        } else {
                            Pesanan i = getPesananList().get(getIndex());
                            if (menu(i).getTipe().equals("ramen"))
                                setText(item + " lv." + i.getLevel());
                            else setText(item);
                        }
                    }
                };
            }
        });

        terimaCol.setCellFactory(new Callback<TreeTableColumn<Pesanan, String>, TreeTableCell<Pesanan, String>>() {
            @Override
            public TreeTableCell<Pesanan, String> call(TreeTableColumn<Pesanan, String> param) {
                return new TreeTableCell<Pesanan, String>() {
                    final JFXButton button = new JFXButton("Terima");
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            button.getStyleClass().add("terima");
                            button.setOnAction(event -> {
                                Pesanan i = pesananTableView.getRoot().getChildren().get(getIndex()).getValue();
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

        tolakCol.setCellFactory(new Callback<TreeTableColumn<Pesanan, String>, TreeTableCell<Pesanan, String>>() {
            @Override
            public TreeTableCell<Pesanan, String> call(TreeTableColumn<Pesanan, String> param) {
                return new TreeTableCell<Pesanan, String>() {
                    final JFXButton button = new JFXButton("Tolak");
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            button.getStyleClass().add("tolak");
                            button.setOnAction(event -> {
                                Dialog alert = new Dialog((Stage) pesananTableView.getScene().getWindow());
                                Pesanan i = pesananTableView.getRoot().getChildren().get(getIndex()).getValue();

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

        Predicate<Pesanan> predicate = item -> item.getStatus_item().equals("dipesan");
        FilteredList<Pesanan> filteredList = new FilteredList<>(getPesananList(), predicate);
        getPesananList().addListener((ListChangeListener<Pesanan>) c -> filteredList.setPredicate(predicate));

        TreeItem<Pesanan> rootItem = new RecursiveTreeItem<>(filteredList, RecursiveTreeObject::getChildren);

        pesananTableView.setRoot(rootItem);
        pesananTableView.getColumns().add(mejaCol);
        pesananTableView.getColumns().add(namaCol);
        pesananTableView.getColumns().add(jumlahCol);
        pesananTableView.getColumns().add(terimaCol);
        pesananTableView.getColumns().add(tolakCol);
        pesananTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        TreeTableColumn<Transaksi, String> mejaTransaksiCol = new TreeTableColumn<>("No Meja");
        TreeTableColumn<Transaksi, String> totalCol = new TreeTableColumn<>("Total Harga");
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
                            button.getStyleClass().add("print-20");
                            button.setOnAction(event -> {
                                Thread thread = new Thread(() -> {
                                    Transaksi transaksi = getTransaksiList().get(getIndex());
                                    try {
                                        Laporan.bill(transaksi);
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                });
                                thread.start();
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
                            button.getStyleClass().add("print-20");
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
                                            Thread thread = new Thread(() -> {
                                                Transaksi transaksi = getTransaksiList().get(getIndex());
                                                try {
                                                    int tunai = Integer.parseInt(tunaiField.getText());
                                                    if (tunai >= transaksi.getTotalBayar())
                                                        Laporan.struk(transaksi, tunai);
                                                    else jumlahTunaiDialog.information(
                                                            "Error",
                                                            "Jumlah tunai tidak mencukupi total pembayaran");
                                                } catch (IOException ex) {
                                                    ex.printStackTrace();
                                                }
                                            });
                                            thread.start();
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
                            button.getStyleClass().add("simpan");
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
