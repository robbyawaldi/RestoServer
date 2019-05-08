package com.unindra.restoserver.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ItemService {
    private static final ObservableList<Pesanan> PESANANS = FXCollections.observableArrayList();
    private static final AtomicInteger count = new AtomicInteger(0);
    private static StatusItem status = StatusItem.CHANGED;

    // Getter
    public static ObservableList<Pesanan> getPesanans() {
        return PESANANS;
    }

    public static List<Pesanan> getItems(String no_meja) {
        return PESANANS.stream().filter(item -> item.getNo_meja().equals(no_meja)).collect(Collectors.toList());
    }

    public static List<Pesanan> getItems(Transaksi transaksi) {
        return PESANANS.stream()
                .filter(item -> item.getNo_meja().equals(transaksi.getNo_meja()))
                .collect(Collectors.toList());
    }

    public static StatusItem getStatus() {
        return status;
    }

    // Add
    public static void add(Pesanan pesanan) {
        pesanan.setId_pesanan(count.getAndIncrement());
        PESANANS.add(pesanan);
        status = StatusItem.CHANGED;
    }

    // Update
    public static boolean update(Pesanan pesanan) {
        Pesanan toEdit = PESANANS.stream().filter(i -> i.getId_pesanan() == pesanan.getId_pesanan()).findFirst().orElse(null);
        if (toEdit != null) {
            PESANANS.set(PESANANS.indexOf(toEdit), pesanan);
            status = StatusItem.CHANGED;
            return true;
        } else return false;
    }

    // Delete
    public static boolean delete(Pesanan pesanan) {
        Pesanan toDelete = PESANANS.stream().filter(i -> i.getId_pesanan() == pesanan.getId_pesanan()).findFirst().orElse(null);
        if (toDelete != null) {
            PESANANS.remove(toDelete);
            status = StatusItem.CHANGED;
            return true;
        } else return false;
    }

    // Setter
    public static void setStatus(StatusItem status) {
        ItemService.status = status;
    }

    // Enum
    public enum StatusItem {
        CHANGED, STILL
    }
}
