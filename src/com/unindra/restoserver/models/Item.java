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

public class Item extends RecursiveTreeObject<Item> {
    private int id_transaksi;
    private int id_item;
    private int id_menu;
    private int jumlah_item;
    private int level_item;
    private String no_meja;
    private String status_item;

    public Item(int id_item, int id_menu, int jumlah_item, int lvl_item, String no_meja, String status_item) {
        this.id_item = id_item;
        this.id_menu = id_menu;
        this.jumlah_item = jumlah_item;
        this.level_item = lvl_item;
        this.no_meja = no_meja;
        this.status_item = status_item;
    }

    private static List<Item> getItems() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "SELECT * FROM `item`";
            return connection.createQuery(query).executeAndFetch(Item.class);
        }
    }

    static List<Item> getItems(Transaksi transaksi) {
        return getItems()
                .stream()
                .filter(item -> item.getId_transaksi() == transaksi.getId_transaksi())
                .collect(Collectors.toList());
    }

    public static List<Item> getItems(Menu menu, List<Transaksi> transaksiList) {
        List<Item> items = getItems(menu);
        List<Item> filterItems = FXCollections.observableArrayList();
        for (Transaksi transaksi : transaksiList) {
            filterItems.addAll(
                    items.stream()
                            .filter(item -> item.id_transaksi == transaksi.getId_transaksi())
                            .collect(Collectors.toList()));
        }
        return filterItems;
    }

    public static List<Item> getItems(Menu menu) {
        return getItems()
                .stream()
                .filter(item -> item.getId_menu() == menu.getId_menu())
                .collect(Collectors.toList());
    }

    public void terima() {
        status_item = "diproses";
    }

    void simpan(int id_transaksi) {
        this.id_transaksi = id_transaksi;
        try (Connection connection = DB.sql2o.open()) {
            final String query = "INSERT INTO `item` (`id_transaksi`,`id_menu`,`jumlah_item`,`level_item`)" +
                    " VALUES (:id_transaksi,:id_menu,:jumlah_item,:level_item)";
            connection.createQuery(query).bind(this).executeUpdate();
        }
    }

    public int getTotal() {
        return (menu(this).getHarga_menu() + level(level_item).getHarga_level()) * jumlah_item;
    }

    @SuppressWarnings("WeakerAccess")
    public int getId_transaksi() {
        return id_transaksi;
    }

    @SuppressWarnings("unused")
    public int getJumlah_item() {
        return jumlah_item;
    }

    int getId_item() {
        return id_item;
    }

    void setId_item(int id_item) {
        this.id_item = id_item;
    }

    int getId_menu() {
        return id_menu;
    }

    public int getLevel_item() {
        return level_item;
    }

    public String getNo_meja() {
        return no_meja;
    }

    public String getStatus_item() {
        return status_item;
    }

    public ObjectProperty<Integer> jumlahProperty() {
        return new SimpleObjectProperty<>(jumlah_item);
    }

    public StringProperty no_mejaProperty() {
        return new SimpleStringProperty(no_meja);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id_transaksi=" + id_transaksi +
                ", id_item=" + id_item +
                ", id_menu=" + id_menu +
                ", jumlah_item=" + jumlah_item +
                ", level_item=" + level_item +
                ", no_meja='" + no_meja + '\'' +
                ", status_item='" + status_item + '\'' +
                '}';
    }
}
