package com.unindra.restoserver.models;

import com.google.gson.annotations.Expose;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.DB;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.joda.time.LocalDate;
import org.sql2o.Connection;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static com.unindra.restoserver.Rupiah.rupiah;
import static com.unindra.restoserver.models.Transaksi.getTransaksiList;

public class Menu extends RecursiveTreeObject<Menu> {
    private String nama_menu;
    private String tipe;
    private int harga_menu;
    @Expose
    private static ObservableList<Menu> menus = FXCollections.observableArrayList();

    static {
        updateMenu();
    }

    // Constructor
    public Menu(String nama_menu, String tipe, int harga_menu) {
        this.nama_menu = nama_menu;
        this.tipe = tipe;
        this.harga_menu = harga_menu;
    }

    public static ObservableList<Menu> getMenus() {
        return menus;
    }

    // Sinkronisasi collections dengan database
    private static void updateMenu() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "SELECT * FROM `menu`";
            menus.setAll(connection.createQuery(query).executeAndFetch(Menu.class));
        }
    }

    // add update delete
    public boolean add() {
        try (Connection connection = DB.sql2o.open()) {
            final String query =
                    "INSERT INTO `menu` (`nama_menu`, `tipe`, `harga_menu`) " +
                    "VALUES (:nama_menu, :tipe, :harga_menu)";
            connection.createQuery(query).bind(this).executeUpdate();
            if (connection.getResult() > 0) {
                updateMenu();
                return true;
            }
            return false;
        }
    }

    public boolean update() {
        try (Connection connection = DB.sql2o.open()) {
            final String query =
                    "UPDATE `menu` SET `tipe` = :tipe, `harga_menu` = :harga_menu " +
                    "WHERE `nama_menu` = :nama_menu";
            connection.createQuery(query).bind(this).executeUpdate();
            if (connection.getResult() > 0) {
                updateMenu();
                return true;
            }
            return false;
        }
    }

    public boolean delete() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "DELETE FROM `menu` WHERE `nama_menu` = :nama_menu";
            connection.createQuery(query).bind(this).executeUpdate();
            if (connection.getResult() > 0) {
                updateMenu();
                return true;
            }
            return false;
        }
    }

    // Getter
    public static Menu menu(LocalDate localDate) {
        AtomicReference<Menu> menufav = new AtomicReference<>(
                new Menu("tidak ada", "", 0));
        AtomicInteger jumlahAtomic = new AtomicInteger();
        for (Menu menu : getMenus()) {
            int jumlah = Pesanan.getPesanan(menu, getTransaksiList(localDate)).size();
            if (jumlahAtomic.get() < jumlah) {
                menufav.set(menu);
                jumlahAtomic.set(jumlah);
            }
        }
        return menufav.get();
    }

    public static Menu menu(Pesanan pesanan) {
        return getMenus()
                .stream()
                .filter(menu -> menu.getNama_menu().equals(pesanan.getNama_menu()))
                .findFirst()
                .orElse(null);
    }

    public String getNama_menu() {
        return nama_menu;
    }

    public String getTipe() {
        return tipe;
    }

    public int getHarga_menu() {
        return harga_menu;
    }

    // Setter
    public void setNama_menu(String nama_menu) {
        this.nama_menu = nama_menu;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public void setHarga_menu(int harga_menu) {
        this.harga_menu = harga_menu;
    }

    // Property
    public StringProperty nama_menuProperty() {
        return new SimpleStringProperty(nama_menu);
    }

    public StringProperty tipeProperty() {
        return new SimpleStringProperty(tipe);
    }

    public StringProperty harga_menuProperty() {
        return new SimpleStringProperty(rupiah(harga_menu));
    }

    @Override
    public String toString() {
        return "Menu{" +
                ", nama_menu='" + nama_menu + '\'' +
                ", harga_menu=" + harga_menu +
                ", tipe='" + tipe + '\'' +
                '}';
    }
}
