package com.unindra.restoserver.models;

import com.unindra.restoserver.DB;
import org.sql2o.Connection;

import java.util.Arrays;

public class DetailRamen {
    private String nama_menu;
    private byte[] foto;
    private String deskripsi;

    private DetailRamen(String nama_menu, byte[] foto, String deskripsi) {
        this.nama_menu = nama_menu;
        this.foto = foto;
        this.deskripsi = deskripsi;
    }

    public DetailRamen(String nama_menu) {
        this(nama_menu, null, "");
    }

    public static DetailRamen detailRamen(DetailRamen detailRamen) {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "SELECT * FROM `detail_ramen` WHERE `nama_menu` = :nama_menu";
            return connection.createQuery(query).bind(detailRamen).executeAndFetchFirst(DetailRamen.class);
        }
    }

    @SuppressWarnings("unused")
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

    @Override
    public String toString() {
        return "DetailRamen{" +
                "nama_menu='" + nama_menu + '\'' +
                ", foto=" + Arrays.toString(foto) +
                ", deskripsi='" + deskripsi + '\'' +
                '}';
    }
}
