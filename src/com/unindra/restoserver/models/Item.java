package com.unindra.restoserver.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public class Item extends RecursiveTreeObject<Item> {
    private int id_item;
    private int id_menu;
    private int jumlah_item;
    private int no_meja;
    private String status_item;

    public Item(int idMenu, int jumlahItem, int noMeja, String statusItem) {
        this.id_menu = idMenu;
        this.jumlah_item = jumlahItem;
        this.no_meja = noMeja;
        this.status_item = statusItem;
    }

    public Item() {
    }

    public int getId_item() {
        return id_item;
    }

    public void setId_item(int id_item) {
        this.id_item = id_item;
    }

    public int getId_menu() {
        return id_menu;
    }

    public void setId_menu(int id_menu) {
        this.id_menu = id_menu;
    }

    public int getJumlah_item() {
        return jumlah_item;
    }

    public void setJumlah_item(int jumlah_item) {
        this.jumlah_item = jumlah_item;
    }

    public int getNo_meja() {
        return no_meja;
    }

    public void setNo_meja(int no_meja) {
        this.no_meja = no_meja;
    }

    public String getStatus_item() {
        return status_item;
    }

    public void setStatus_item(String status_item) {
        this.status_item = status_item;
    }

    public ObjectProperty<Integer> jumlahProperty() {
        return new SimpleObjectProperty<>(jumlah_item);
    }

    @Override
    public String toString() {
        return "Item{" +
                "id_item=" + id_item +
                ", id_menu=" + id_menu +
                ", jumlah_item=" + jumlah_item +
                ", no_meja=" + no_meja +
                ", status_item='" + status_item + '\'' +
                '}';
    }
}
