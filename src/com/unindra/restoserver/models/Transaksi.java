package com.unindra.restoserver.models;

import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.DB;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.joda.time.LocalDate;
import org.sql2o.Connection;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.unindra.restoserver.Rupiah.rupiah;

public class Transaksi extends RecursiveTreeObject<Transaksi> {
    private int id_transaksi;
    private String no_meja;
    private Date tanggal;

    // Constructor
    public Transaksi(String no_meja) {
        this.no_meja = no_meja;
        this.tanggal = new Date();
    }

    // Simpan
    public void simpan() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "INSERT INTO `transaksi` (`no_meja`,`tanggal`) VALUES (:no_meja,:tanggal)";
            connection.createQuery(query).bind(this).executeUpdate();
            this.id_transaksi = connection.getKey(Integer.class);
        }
        List<Item> items = ItemService.getItems(no_meja);
        items.forEach(item -> item.simpan(id_transaksi));
        TransaksiService.delete(this);
        items.forEach(ItemService::delete);
    }

    // Getter
    public int getTotalBayar() {
        return Item.getItems(this).stream().mapToInt(Item::getTotal).sum();
    }

    private static List<Transaksi> getTransaksiList() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "SELECT * FROM `transaksi`";
            return connection.createQuery(query).executeAndFetch(Transaksi.class);
        }
    }

    public static List<Transaksi> getTransaksiList(LocalDate tanggal) {
        return getTransaksiList()
                .stream()
                .filter(transaksi -> new LocalDate(transaksi.getTanggal()).equals(tanggal))
                .collect(Collectors.toList());
    }

    public static List<Transaksi> getTransaksiList(int tahun, int bulan) {
        return getTransaksiList()
                .stream()
                .filter(transaksi -> {
                    LocalDate localDate = new LocalDate(transaksi.getTanggal());
                    return localDate.getYear() == tahun && localDate.getMonthOfYear() == bulan;
                })
                .collect(Collectors.toList());
    }

    public int getId_transaksi() {
        return id_transaksi;
    }

    public String getNo_meja() {
        return no_meja;
    }

    private Date getTanggal() {
        return tanggal;
    }

    // Property
    public StringProperty no_mejaProperty() {
        return new SimpleStringProperty(no_meja);
    }

    public StringProperty totalProperty() {
        return new SimpleStringProperty(rupiah(ItemService.getTotalBayar(this)));
    }

    @Override
    public String toString() {
        return "Transaksi{" +
                "id_transaksi=" + id_transaksi +
                ", no_meja='" + no_meja + '\'' +
                ", tanggal=" + tanggal +
                '}';
    }
}
