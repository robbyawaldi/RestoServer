package com.unindra.restoserver.models;

import com.google.gson.annotations.Expose;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.DB;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.sql2o.Connection;

import static com.unindra.restoserver.Rupiah.rupiah;

public class Level extends RecursiveTreeObject<Level> {
    private int level_item;
    private int harga_level;
    @Expose
    private static ObservableList<Level> levels = FXCollections.observableArrayList();

    // Constructor
    private Level(int level, int harga_level) {
        this.level_item = level;
        this.harga_level = harga_level;
    }

    static {
        updateLevel();
    }

    // Sinkronisasi collection dengan database
    private static void updateLevel() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "SELECT * FROM `level`";
            levels.setAll(connection.createQuery(query).executeAndFetch(Level.class));
        }
    }

    // Update Level
    public boolean update() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "UPDATE `level` SET `harga_level` = :harga_level WHERE `level_item` = :level_item";
            connection.createQuery(query).bind(this).executeUpdate();
            if (connection.getResult() > 0) {
                updateLevel();
                return true;
            }
            return false;
        }
    }

    // Getter
    public static ObservableList<Level> getLevels() {
        return levels;
    }

    static Level level(int level) {
        return levels.stream().filter(l -> l.level_item == level).findFirst().orElse(null);
    }

    public int getLevel_item() {
        return level_item;
    }

    public int getHarga_level() {
        return harga_level;
    }

    // Setter
    public void setHarga_level(int harga_level) {
        this.harga_level = harga_level;
    }

    // Property
    public ObjectProperty<Integer> levelProperty() {
        return new SimpleObjectProperty<>(level_item);
    }

    public StringProperty hargaProperty() {
        return new SimpleStringProperty(rupiah(harga_level));
    }

}
