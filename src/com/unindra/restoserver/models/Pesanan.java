package com.unindra.restoserver.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.DB;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import org.sql2o.Connection;

import java.util.List;
import java.util.stream.Collectors;

import static com.unindra.restoserver.models.Level.level;
import static com.unindra.restoserver.models.Menu.menu;

public class Pesanan extends RecursiveTreeObject<Pesanan> {
    private int id_transaksi;
    private int id_pesanan;
    private int id_menu;
    private int jumlah;
    private int level;
    private String no_meja;
    private String status_item;

    // Constructor
    public Pesanan(int id_pesanan, int id_menu, int jumlah, int lvl_item, String no_meja, String status_item) {
        this.id_pesanan = id_pesanan;
        this.id_menu = id_menu;
        this.jumlah = jumlah;
        this.level = lvl_item;
        this.no_meja = no_meja;
        this.status_item = status_item;
    }

    // Terima
    public void terima() {
        status_item = "diproses";
    }

    // Simpan
    void simpan(Transaksi transaksi) {
        this.id_transaksi = transaksi.getId_transaksi();
        try (Connection connection = DB.sql2o.open()) {
            final String query = "INSERT INTO `pesanan` (`id_transaksi`,`id_menu`,`jumlah`,`level`)" +
                    " VALUES (:id_transaksi,:id_menu,:jumlah,:level)";
            connection.createQuery(query).bind(this).executeUpdate();
        }
    }

    // Getter
    private static List<Pesanan> getItems() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "SELECT * FROM `pesanan`";
            return connection.createQuery(query).executeAndFetch(Pesanan.class);
        }
    }

    static List<Pesanan> getItems(Transaksi transaksi) {
        return getItems()
                .stream()
                .filter(item -> item.id_transaksi == transaksi.getId_transaksi())
                .collect(Collectors.toList());
    }

    public static List<Pesanan> getItems(Menu menu, List<Transaksi> transaksiList) {
        List<Pesanan> pesanans = getItems(menu);
        List<Pesanan> filterPesanans = FXCollections.observableArrayList();
        for (Transaksi transaksi : transaksiList) {
            filterPesanans.addAll(
                    pesanans.stream()
                            .filter(item -> item.id_transaksi == transaksi.getId_transaksi())
                            .collect(Collectors.toList()));
        }
        return filterPesanans;
    }

    public static List<Pesanan> getItems(Menu menu) {
        return getItems()
                .stream()
                .filter(item -> item.getId_menu() == menu.getId_menu())
                .collect(Collectors.toList());
    }

    public int getTotal() {
        return (menu(this).getHarga() + level(level).getHarga()) * jumlah;
    }

    @SuppressWarnings("unused")
    int getId_transaksi() {
        return id_transaksi;
    }

    public int getJumlah() {
        return jumlah;
    }

    int getId_pesanan() {
        return id_pesanan;
    }

    int getId_menu() {
        return id_menu;
    }

    public int getLevel() {
        return level;
    }

    public String getNo_meja() {
        return no_meja;
    }

    public String getStatus_item() {
        return status_item;
    }

    // Setter
    void setId_pesanan(int id_pesanan) {
        this.id_pesanan = id_pesanan;
    }

    // Property
    public ObjectProperty<Integer> jumlahProperty() {
        return new SimpleObjectProperty<>(jumlah);
    }

    public StringProperty no_mejaProperty() {
        return new SimpleStringProperty(no_meja);
    }

    // toString
    @Override
    public String toString() {
        return "Pesanan{" +
                "id_transaksi=" + id_transaksi +
                ", id_pesanan=" + id_pesanan +
                ", id_menu=" + id_menu +
                ", jumlah=" + jumlah +
                ", level=" + level +
                ", no_meja='" + no_meja + '\'' +
                ", status_item='" + status_item + '\'' +
                '}';
    }
}
