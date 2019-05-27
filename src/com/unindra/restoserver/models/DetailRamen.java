package com.unindra.restoserver.models;

import com.unindra.restoserver.DB;
import org.sql2o.Connection;

import java.util.Arrays;
import java.util.List;

public class DetailRamen {
    private String nama_menu;
    private byte[] foto;
    private String deskripsi;

    private DetailRamen(String nama_menu, byte[] foto, String deskripsi) {
        this.nama_menu = nama_menu;
        this.foto = foto;
        this.deskripsi = deskripsi;
    }

    public DetailRamen(byte[] foto) {
        this("", foto, "");
    }

    private static List<DetailRamen> detailRamen() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "SELECT * FROM `detail_ramen`";
            return connection.createQuery(query).executeAndFetch(DetailRamen.class);
        }
    }

    public static DetailRamen detailRamen(String nama_menu) {
        return detailRamen()
                .stream()
                .filter(detailRamen -> detailRamen.getNama_menu().equals(nama_menu))
                .findFirst()
                .orElse(null);
    }

    public static DetailRamen detailRamen(Menu menu) {
        return detailRamen()
                .stream()
                .filter(detailRamen -> detailRamen.getNama_menu().equals(menu.getNama_menu()))
                .findFirst()
                .orElse(null);
    }

    public boolean add() {
        try (Connection connection = DB.sql2o.open()) {
            final String query =
                    "INSERT INTO `detail_ramen` (`nama_menu`, `foto`, `deskripsi`) " +
                    "VALUES (:nama_menu, :foto, :deskripsi)";
            connection.createQuery(query).bind(this).executeUpdate();
            return connection.getResult() > 0;
        }
    }

    public boolean update() {
        try (Connection connection = DB.sql2o.open()) {
            final String query =
                    "UPDATE `detail_ramen` SET `foto` = :foto, `deskripsi` = :deskripsi " +
                    "WHERE `nama_menu` = :nama_menu";
            connection.createQuery(query).bind(this).executeUpdate();
            return connection.getResult() > 0;
        }
    }

    public boolean delete() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "DELETE FROM `detail_ramen` WHERE `nama_menu` = :nama_menu";
            connection.createQuery(query).bind(this).executeUpdate();
            return connection.getResult() > 0;
        }
    }

    @SuppressWarnings("WeakerAccess")
    public String getNama_menu() {
        return nama_menu;
    }

    @SuppressWarnings("unused")
    public byte[] getFoto() {
        return foto;
    }

    @SuppressWarnings("unused")
    public String getDeskripsi() {
        return deskripsi;
    }

    public void setNama_menu(String nama_menu) {
        this.nama_menu = nama_menu;
    }

    public void setDeskripsi(String deskripsi) {
        this.deskripsi = deskripsi;
    }

    @Override
    public String toString() {
        return "DetailRamen{" +
                "nama_menu='" + nama_menu + '\'' +
                ", foto=" + Arrays.toString(foto) +
                ", deskripsi='" + deskripsi + '\'' +
                '}';
    }

}
