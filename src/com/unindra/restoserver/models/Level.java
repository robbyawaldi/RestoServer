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
    private int level;
    private int harga;
    @Expose
    private static ObservableList<Level> levels = FXCollections.observableArrayList();

    // Constructor
    private Level(int level, int harga) {
        this.level = level;
        this.harga = harga;
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
            final String query = "UPDATE `level` SET `harga` = :harga WHERE `level` = :level";
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
        return levels.stream().filter(l -> l.level == level).findFirst().orElse(null);
    }

    public int getLevel() {
        return level;
    }

    public int getHarga() {
        return harga;
    }

    // Setter
    public void setHarga(int harga) {
        this.harga = harga;
    }

    // Property
    public ObjectProperty<Integer> levelProperty() {
        return new SimpleObjectProperty<>(level);
    }

    public StringProperty hargaProperty() {
        return new SimpleStringProperty(rupiah(harga));
    }

}
