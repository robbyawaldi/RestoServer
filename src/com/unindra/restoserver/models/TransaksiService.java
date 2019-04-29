package com.unindra.restoserver.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class TransaksiService {
    private static ObservableList<Transaksi> transaksiList = FXCollections.observableArrayList();

    public static ObservableList<Transaksi> getTransaksiList() {
        return transaksiList;
    }

    public static void add(Transaksi transaksi) {
        if (transaksiList.stream().noneMatch(t -> t.getNo_meja().equals(transaksi.getNo_meja()))) {
            transaksiList.add(transaksi);
        }
    }

    static void delete(Transaksi transaksi) {
        transaksiList.remove(transaksi);
    }

}
