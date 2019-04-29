package com.unindra.restoserver.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ItemService {
    private static final ObservableList<Item> items = FXCollections.observableArrayList();
    private static final AtomicInteger count = new AtomicInteger(0);
    private static StatusItem status = StatusItem.CHANGED;

    // Getter
    public static ObservableList<Item> getItems() {
        return items;
    }

    public static List<Item> getItems(String no_meja) {
        return items.stream().filter(item -> item.getNo_meja().equals(no_meja)).collect(Collectors.toList());
    }

    public static List<Item> getItems(Transaksi transaksi) {
        return items.stream()
                .filter(item -> item.getId_transaksi() == transaksi.getId_transaksi())
                .collect(Collectors.toList());
    }

    public static StatusItem getStatus() {
        return status;
    }

    // Add
    public static void add(Item item) {
        item.setId_item(count.getAndIncrement());
        items.add(item);
        status = StatusItem.CHANGED;
    }

    // Update
    public static boolean update(Item item) {
        Item toEdit = items.stream().filter(i -> i.getId_item() == item.getId_item()).findFirst().orElse(null);
        if (toEdit != null) {
            items.set(items.indexOf(toEdit), item);
            status = StatusItem.CHANGED;
            return true;
        } else return false;
    }

    // Delete
    public static boolean delete(Item item) {
        Item toDelete = items.stream().filter(i -> i.getId_item() == item.getId_item()).findFirst().orElse(null);
        if (toDelete != null) {
            items.remove(toDelete);
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
