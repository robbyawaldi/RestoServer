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

    public boolean simpan() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "INSERT INTO `transaksi` (`no_meja`,`tanggal`) VALUES (:no_meja,:tanggal)";
            id_transaksi = connection.createQuery(query).executeUpdate().getKey(Integer.class);
            return connection.getResult() > 0;
        }
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
