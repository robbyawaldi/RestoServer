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

import java.util.List;
import java.util.stream.Collectors;

import static com.unindra.restoserver.Rupiah.rupiah;
import static com.unindra.restoserver.models.Level.level;
import static com.unindra.restoserver.models.Menu.menu;

public class Pesanan extends RecursiveTreeObject<Pesanan> {
    private String id_transaksi;
    private String id_pesanan;
    private String nama_menu;
    private int jumlah;
    private int level;
    private String no_meja;
    private String status_item;
    @Expose
    private static ObservableList<Pesanan> pesananList = FXCollections.observableArrayList();

    // Constructor
    public Pesanan(String id_pesanan, String nama_menu, int jumlah, int lvl_item, String no_meja, String status_item) {
        this.id_pesanan = id_pesanan;
        this.nama_menu = nama_menu;
        this.jumlah = jumlah;
        this.level = lvl_item;
        this.no_meja = no_meja;
        this.status_item = status_item;
    }

    public static ObservableList<Pesanan> getPesananList() {
        return pesananList;
    }

    // Terima
    public void terima() {
        status_item = "diproses";
    }

    // Simpan
    void simpan(Transaksi transaksi) {
        this.id_transaksi = transaksi.getId_transaksi();
        try (Connection connection = DB.sql2o.open()) {
            final String query =
                    "INSERT INTO `pesanan` (`id_pesanan`,`id_transaksi`,`nama_menu`,`jumlah`,`level`) " +
                            "VALUES (:id_pesanan,:id_transaksi,:nama_menu,:jumlah,:level)";
            connection.createQuery(query).bind(this).executeUpdate();
        }
    }

    // Getter
    static List<Pesanan> getPesanan() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "SELECT * FROM `pesanan`";
            return connection.createQuery(query).executeAndFetch(Pesanan.class);
        }
    }

    public static List<Pesanan> getPesanan(Transaksi transaksi) {
        return getPesanan()
                .stream()
                .filter(item -> item.id_transaksi.equals(transaksi.getId_transaksi()))
                .collect(Collectors.toList());
    }

    public static List<Pesanan> getPesanan(Transaksi transaksi, Menu menu) {
        return getPesanan()
                .stream()
                .filter(item -> item.id_transaksi.equals(transaksi.getId_transaksi()))
                .collect(Collectors.toList())
                .stream()
                .filter(pesanan -> pesanan.getNama_menu().equals(menu.getNama_menu()))
                .collect(Collectors.toList());
    }

    public static List<Pesanan> getPesanan(Menu menu, List<Transaksi> transaksiList) {
        List<Pesanan> pesanans = getPesanan(menu);
        List<Pesanan> filterPesanans = FXCollections.observableArrayList();
        for (Transaksi transaksi : transaksiList) {
            filterPesanans.addAll(
                    pesanans.stream()
                            .filter(item -> item.id_transaksi.equals(transaksi.getId_transaksi()))
                            .collect(Collectors.toList()));
        }
        return filterPesanans;
    }

    public static List<Pesanan> getPesanan(Menu menu) {
        return getPesanan()
                .stream()
                .filter(pesanan -> pesanan.getNama_menu().equals(menu.getNama_menu()))
                .collect(Collectors.toList());
    }

    public int getTotal() {
        return (menu(this).getHarga_menu() + level(level).getHarga_level()) * jumlah;
    }

    @SuppressWarnings("unused")
    String getId_transaksi() {
        return id_transaksi;
    }

    public int getJumlah() {
        return jumlah;
    }

    String getId_pesanan() {
        return id_pesanan;
    }

    public String getNama_menu() {
        return nama_menu;
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
    void setId_pesanan(String id_pesanan) {
        this.id_pesanan = id_pesanan;
    }

    // Property
    public ObjectProperty<Integer> jumlahProperty() {
        return new SimpleObjectProperty<>(jumlah);
    }

    public StringProperty no_mejaProperty() {
        return new SimpleStringProperty(no_meja);
    }

    public StringProperty totalHargaProperty() {
        return new SimpleStringProperty(rupiah(getTotal()));
    }

    // toString
    @Override
    public String toString() {
        return "Pesanan{" +
                "id_transaksi=" + id_transaksi +
                ", id_pesanan=" + id_pesanan +
                ", nama_menu=" + nama_menu +
                ", jumlah=" + jumlah +
                ", level=" + level +
                ", no_meja='" + no_meja + '\'' +
                ", status_item='" + status_item + '\'' +
                '}';
    }
}
