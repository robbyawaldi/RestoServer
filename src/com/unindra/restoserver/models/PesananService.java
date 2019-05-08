package com.unindra.restoserver.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class PesananService {
    private static final ObservableList<Pesanan> pesananList = FXCollections.observableArrayList();
    private static final AtomicInteger count = new AtomicInteger(0);
    private static StatusPesanan status = StatusPesanan.CHANGED;

    // Getter
    public static ObservableList<Pesanan> getPesananList() {
        return pesananList;
    }

    public static List<Pesanan> getItems(String no_meja) {
        return pesananList.stream().filter(item -> item.getNo_meja().equals(no_meja)).collect(Collectors.toList());
    }

    public static List<Pesanan> getItems(Transaksi transaksi) {
        return pesananList.stream()
                .filter(item -> item.getNo_meja().equals(transaksi.getNo_meja()))
                .collect(Collectors.toList());
    }

    public static StatusPesanan getStatus() {
        return status;
    }

    // Add
    public static void add(Pesanan pesanan) {
        pesanan.setId_pesanan(count.getAndIncrement());
        pesananList.add(pesanan);
        status = StatusPesanan.CHANGED;
    }

    // Update
    public static boolean update(Pesanan pesanan) {
        Pesanan toEdit = pesananList.stream().filter(i -> i.getId_pesanan() == pesanan.getId_pesanan()).findFirst().orElse(null);
        if (toEdit != null) {
            pesananList.set(pesananList.indexOf(toEdit), pesanan);
            status = StatusPesanan.CHANGED;
            return true;
        } else return false;
    }

    // Delete
    public static boolean delete(Pesanan pesanan) {
        Pesanan toDelete = pesananList.stream().filter(i -> i.getId_pesanan() == pesanan.getId_pesanan()).findFirst().orElse(null);
        if (toDelete != null) {
            pesananList.remove(toDelete);
            status = StatusPesanan.CHANGED;
            return true;
        } else return false;
    }

    // Setter
    public static void setStatus(StatusPesanan status) {
        PesananService.status = status;
    }

    // Enum
    public enum StatusPesanan {
        CHANGED, STILL
    }
}
