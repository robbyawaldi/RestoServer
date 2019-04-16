package com.unindra.restoserver.models;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class ItemService {
    private static final ObservableList<Item> items = FXCollections.observableArrayList();
    private static final AtomicInteger count = new AtomicInteger(0);

    public static ObservableList<Item> getItems() {
        return items;
    }

    public static List<Item> getItems(String no_meja) {
        return items.stream().filter(item -> item.getNo_meja().equals(no_meja)).collect(Collectors.toList());
    }

    public static void add(Item item) {
        item.setId_item(count.getAndIncrement());
        items.add(item);
    }

    public static boolean update(Item item) {
        Item toEdit = items.stream().filter(i -> i.getId_item() == item.getId_item()).findFirst().orElse(null);
        if (toEdit != null) {
            items.set(items.indexOf(toEdit), item);
            return true;
        } else return false;
    }

    public static boolean delete(Item item) {
        Item toDelete = items.stream().filter(i -> i.getId_item() == item.getId_item()).findFirst().orElse(null);
        if (toDelete != null) {
            items.remove(toDelete);
            return true;
        } else return false;
    }
}
