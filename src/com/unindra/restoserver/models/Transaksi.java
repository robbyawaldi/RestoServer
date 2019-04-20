package com.unindra.restoserver.models;

import com.google.gson.annotations.Expose;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.DB;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.sql2o.Connection;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.unindra.restoserver.models.ItemService.getItems;

public class Transaksi extends RecursiveTreeObject<Transaksi> {
    private int id_transaksi;
    private String no_meja;
    private Date tanggal;
    @Expose
    private static ObservableList<Transaksi> transaksiList = FXCollections.observableArrayList();

    public Transaksi(String no_meja) {
        this.no_meja = no_meja;
        this.tanggal = new Date();
    }

    private int getTotalHarga() {
        List<Item> items = getItems()
                .stream()
                .filter(item -> item.getNo_meja().equals(no_meja))
                .collect(Collectors.toList());
        return items.stream().mapToInt(Item::getTotal).sum();
    }

    private List<Transaksi> getTransaksiListFromDB() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "SELECT * FROM `transaksi`";
            return connection.createQuery(query).executeAndFetch(Transaksi.class);
        }
    }

    public void simpan() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "INSERT INTO `transaksi` (`no_meja`,`tanggal`) VALUES (:no_meja,:tanggal)";
            connection.createQuery(query).bind(this).executeUpdate();
            this.id_transaksi = connection.getKey(Integer.class);
        }
        List<Item> items = getItems(no_meja);
        items.forEach(item -> item.simpan(id_transaksi));
        getTransaksiList().remove(this);
        items.forEach(item -> getItems().remove(item));
    }

    public static Transaksi oldTransaksi(Transaksi transaksi) {
        return transaksiList.stream().filter(t -> t.no_meja.equals(transaksi.no_meja)).findFirst().orElse(null);
    }

    @SuppressWarnings("unused")
    public String getNo_meja() {
        return no_meja;
    }

    @SuppressWarnings("unused")
    public Date getTanggal() {
        return tanggal;
    }

    public static ObservableList<Transaksi> getTransaksiList() {
        return transaksiList;
    }

    public StringProperty no_mejaProperty() {
        return new SimpleStringProperty(no_meja);
    }

    public ObjectProperty<Integer> totalProperty() {
        return new SimpleObjectProperty<>(getTotalHarga());
    }

    @Override
    public String toString() {
        return "Transaksi{" +
                "id_transaksi=" + id_transaksi +
                ", no_meja='" + no_meja + '\'' +
                ", tanggal=" + tanggal +
                '}';
    }
}
