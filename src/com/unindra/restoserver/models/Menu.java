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
import static com.unindra.restoserver.models.Pesanan.getItems;
import static com.unindra.restoserver.models.Transaksi.getTransaksiList;

public class Menu extends RecursiveTreeObject<Menu> {
    private int id_menu;
    private String nama;
    private String tipe;
    private int harga_menu;
    private String deskripsi;
    @Expose
    private static ObservableList<Menu> menus = FXCollections.observableArrayList();

    static {
        updateMenu();
    }

    // Constructor
    public Menu(int id_menu, String nama, String type, int harga_menu, String deskripsi) {
        this.id_menu = id_menu;
        this.nama = nama;
        this.tipe = type;
        this.harga_menu = harga_menu;
        this.deskripsi = deskripsi;
    }

    public Menu(String nama, String tipe, int harga_menu, String deskripsi) {
        this.nama = nama;
        this.tipe = tipe;
        this.harga_menu = harga_menu;
        this.deskripsi = deskripsi;
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
            final String query = "INSERT INTO `menu` (`nama`, `tipe`, `harga_menu`, `deskripsi`) " +
                    "VALUES (:nama, :tipe, :harga_menu, :deskripsi)";
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
            final String query = "UPDATE `menu` SET `nama` = :nama, `tipe` = :tipe, " +
                    "`harga_menu` = :harga_menu, `deskripsi` = :deskripsi WHERE `id_menu` = :id_menu";
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
            final String query = "DELETE FROM `menu` WHERE `id_menu` = :id_menu";
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
                new Menu(0, "tidak ada", "", 0, ""));
        AtomicInteger jumlahAtomic = new AtomicInteger();
        for (Menu menu : getMenus()) {
            int jumlah = getItems(menu, getTransaksiList(localDate)).size();
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
                .filter(daftarMenu -> daftarMenu.id_menu == pesanan.getId_menu())
                .findFirst()
                .orElse(null);
    }

    int getId_menu() {
        return id_menu;
    }

    public String getNama() {
        return nama;
    }

    public String getTipe() {
        return tipe;
    }

    public int getHarga_menu() {
        return harga_menu;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    // Setter
    public void setNama(String nama) {
        this.nama = nama;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }

    public void setHarga_menu(int harga_menu) {
        this.harga_menu = harga_menu;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    // Property
    public StringProperty namaProperty() {
        return new SimpleStringProperty(nama);
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
                "id_menu=" + id_menu +
                ", nama='" + nama + '\'' +
                ", harga_menu=" + harga_menu +
                ", tipe='" + tipe + '\'' +
                ", deskripsi='" + deskripsi + '\'' +
                '}';
    }
}
