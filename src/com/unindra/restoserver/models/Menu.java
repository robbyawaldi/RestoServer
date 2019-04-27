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
import static com.unindra.restoserver.models.Item.getItems;
import static com.unindra.restoserver.models.Transaksi.getTransaksiList;

public class Menu extends RecursiveTreeObject<Menu> {
    private int id_menu;
    private String nama_menu;
    private String tipe_menu;
    private int harga_menu;
    private String deskripsi;
    @Expose
    private static ObservableList<Menu> menus = FXCollections.observableArrayList();

    public static ObservableList<Menu> getMenus() {
        updateMenu();
        return menus;
    }

    public Menu(int id_menu, String nama_menu, String type, int harga_menu, String deskripsi) {
        this.id_menu = id_menu;
        this.nama_menu = nama_menu;
        this.tipe_menu = type;
        this.harga_menu = harga_menu;
        this.deskripsi = deskripsi;
    }

    public Menu(String nama_menu, String tipe_menu, int harga_menu, String deskripsi) {
        this.nama_menu = nama_menu;
        this.tipe_menu = tipe_menu;
        this.harga_menu = harga_menu;
        this.deskripsi = deskripsi;
    }

    private static void updateMenu() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "SELECT * FROM `menu`";
            menus.setAll(connection.createQuery(query).executeAndFetch(Menu.class));
        }
    }

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

    public static Menu menu(Item item) {
        return getMenus()
                .stream()
                .filter(daftarMenu -> daftarMenu.id_menu == item.getId_menu())
                .findFirst()
                .orElse(null);
    }

    public boolean tambah() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "INSERT INTO `menu` (`nama_menu`, `tipe_menu`, `harga_menu`, `deskripsi`) " +
                    "VALUES (:nama_menu, :tipe_menu, :harga_menu, :deskripsi)";
            connection.createQuery(query).bind(this).executeUpdate();
            if (connection.getResult() > 0) {
                updateMenu();
                return true;
            }
            return false;
        }
    }

    public boolean hapus() {
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

    public boolean ubah() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "UPDATE `menu` SET `nama_menu` = :nama_menu, `tipe_menu` = :tipe_menu, " +
                    "`harga_menu` = :harga_menu, `deskripsi` = :deskripsi WHERE `id_menu` = :id_menu";
            connection.createQuery(query).bind(this).executeUpdate();
            if (connection.getResult() > 0) {
                updateMenu();
                return true;
            }
            return false;
        }
    }

    int getId_menu() {
        return id_menu;
    }

    public String getNama_menu() {
        return nama_menu;
    }

    public String getTipe_menu() {
        return tipe_menu;
    }

    public int getHarga_menu() {
        return harga_menu;
    }

    public String getDeskripsi() {
        return deskripsi;
    }

    public void setNama_menu(String nama_menu) {
        this.nama_menu = nama_menu;
    }

    public void setTipe_menu(String tipe_menu) {
        this.tipe_menu = tipe_menu;
    }

    public void setHarga_menu(int harga_menu) {
        this.harga_menu = harga_menu;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    public StringProperty namaProperty() {
        return new SimpleStringProperty(nama_menu);
    }

    public StringProperty tipeProperty() {
        return new SimpleStringProperty(tipe_menu);
    }

    public StringProperty hargaProperty() {
        return new SimpleStringProperty(rupiah(harga_menu));
    }

    @Override
    public String toString() {
        return "Menu{" +
                "id_menu=" + id_menu +
                ", nama_menu='" + nama_menu + '\'' +
                ", harga_menu=" + harga_menu +
                ", tipe_menu='" + tipe_menu + '\'' +
                ", deskripsi='" + deskripsi + '\'' +
                '}';
    }
}
