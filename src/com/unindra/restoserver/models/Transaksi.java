package com.unindra.restoserver.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.DB;
import org.sql2o.Connection;

import java.util.Date;

public class Transaksi extends RecursiveTreeObject<Transaksi> {
    private int id_transaksi;
    private Date tanggal;

    public Transaksi() {
        tanggal = new Date();
        try (Connection connection = DB.sql2o.open()) {
            final String query = "INSERT INTO `transaksi` (`tanggal`) VALUES (:tanggal)";
            id_transaksi = connection.createQuery(query).executeUpdate().getKey(Integer.class);
        }
    }

    @Override
    public String toString() {
        return "Transaksi{" +
                "id_transaksi=" + id_transaksi +
                ", tanggal=" + tanggal +
                '}';
    }
}
