package com.unindra.restoserver.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;

import java.util.List;

public class DaftarMenu {
    private int id_menu;
    private String nama_menu;
    private int harga_menu;
    private String type;
    private String deskripsi;

    private DaftarMenu(int id_menu, String nama_menu, int harga_menu, String type, String deskripsi) {
        this.id_menu = id_menu;
        this.nama_menu = nama_menu;
        this.harga_menu = harga_menu;
        this.type = type;
        this.deskripsi = deskripsi;
    }

    public static List<DaftarMenu> menus() {
        return FXCollections.observableArrayList(
                new DaftarMenu(1, "miso", 14000, "ramen", "Enak dan khas jepang deh"),
                new DaftarMenu(2, "nemo", 15000, "ramen", "Enak dan khas jepang deh"),
                new DaftarMenu(3, "teh manis", 15000, "minuman", "Enak dan khas jepang deh"),
                new DaftarMenu(4, "es jeruk", 15000, "minuman", "Enak dan khas jepang deh"),
                new DaftarMenu(5, "mendoan", 15000, "cemilan", "Enak dan khas jepang deh"),
                new DaftarMenu(6, "paket 1", 15000, "lainnya", "Enak dan khas jepang deh"),
                new DaftarMenu(7, "shoyu", 12000, "ramen", "Enak dan khas jepang deh")
        );
    }

    public static DaftarMenu menu(Item item) {
        return menus()
                .stream()
                .filter(daftarMenu -> daftarMenu.id_menu == item.getId_menu())
                .findFirst()
                .orElse(null);
    }

    public StringProperty namaProperty() {
        return new SimpleStringProperty(nama_menu);
    }
}
