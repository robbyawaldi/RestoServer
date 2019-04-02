package com.unindra.restoserver.models;

import com.unindra.restoserver.models.Item;
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

    public static List<Item> getItems(int noMeja) {
        return items.stream().filter(item -> item.getNo_meja() == noMeja).collect(Collectors.toList());
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
        Item toEdit = items.stream().filter(i -> i.getId_item() == item.getId_item()).findFirst().orElse(null);
        if (toEdit != null) {
            items.remove(toEdit);
            return true;
        } else return false;
    }
}
