package com.unindra.restoserver.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

import static com.unindra.restoserver.models.Pesanan.getPesanan;

public class PesananService {
    private static final ObservableList<Pesanan> pesananList = FXCollections.observableArrayList();
    private static final AtomicInteger count;

    static {
        String latestID ;
        if (!getPesanan().isEmpty()) latestID = getPesanan().get(getPesanan().size() - 1).getId_pesanan();
        else latestID = "1";
        count = new AtomicInteger(Integer.parseInt(latestID));
    }

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

    // Add
    public static void add(Pesanan pesanan) {
        pesanan.setId_pesanan(Id.getIdStringFromInt(count.incrementAndGet()));
        pesananList.add(pesanan);
    }

    // Update
    public static boolean update(Pesanan pesanan) {
        Pesanan toEdit = pesananList
                .stream()
                .filter(i -> i.getId_pesanan().equals(pesanan.getId_pesanan()))
                .findFirst()
                .orElse(null);
        if (toEdit != null) {
            pesananList.set(pesananList.indexOf(toEdit), pesanan);
            return true;
        } else return false;
    }

    // Delete
    public static boolean delete(Pesanan pesanan) {
        Pesanan toDelete = pesananList
                .stream()
                .filter(i -> i.getId_pesanan().equals(pesanan.getId_pesanan()))
                .findFirst()
                .orElse(null);
        if (toDelete != null) {
            pesananList.remove(toDelete);
            return true;
        } else return false;
    }
}
