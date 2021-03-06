package com.unindra.restoserver.models;

import com.google.gson.annotations.Expose;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.DB;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.joda.time.*;
import org.sql2o.Connection;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static com.unindra.restoserver.Rupiah.rupiah;

public class Transaksi extends RecursiveTreeObject<Transaksi> {
    private String id_transaksi;
    private String no_meja;
    private Date tanggal;
    @Expose
    private static ObservableList<Transaksi> transaksiList = FXCollections.observableArrayList();

    // Constructor
    public Transaksi(String no_meja) {
        this.id_transaksi = Id.getIdByDateTime(new LocalDateTime());
        this.no_meja = no_meja;
        this.tanggal = new Date();
    }

    static {
        Thread thread = new Thread(() -> {
            while (!Thread.interrupted()) {
                try {
                    updateTransaksi();
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        thread.start();
    }

    // Simpan
    public void simpan() {
        try (Connection connection = DB.sql2o.open()) {
            final String query =
                    "INSERT INTO `transaksi` (`id_transaksi`,`no_meja`,`tanggal`) " +
                            "VALUES (:id_transaksi,:no_meja,:tanggal)";
            connection.createQuery(query).bind(this).executeUpdate();
            if (connection.getResult() > 0) {
                List<Pesanan> pesanans = PesananService.getItems(this);
                pesanans.forEach(pesanan -> pesanan.simpan(this));
                pesanans.forEach(PesananService::delete);
                TransaksiService.delete(this);
            }
        }
    }

    // Sinkronisasi collection dengan database
    private static void updateTransaksi() {
        try (Connection connection = DB.sql2o.open()) {
            final String query = "SELECT * FROM `transaksi`";
            transaksiList.setAll(connection.createQuery(query).executeAndFetch(Transaksi.class));
        }
    }

    // Getter
    public static ObservableList<Transaksi> getTransaksiList() {
        return transaksiList;
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

    public static Transaksi getTransaksi(Pesanan pesanan) {
        return getTransaksiList()
                .stream()
                .filter(transaksi -> transaksi.getId_transaksi().equals(pesanan.getId_transaksi()))
                .findFirst()
                .orElse(null);
    }

    public static int getTotalBayar(int tahun, int bulan) {
        return getTransaksiList(tahun, bulan)
                .stream()
                .mapToInt(Transaksi::getTotalBayar)
                .sum();
    }

    public int getTotalBayar() {
        return Pesanan.getPesanan(this).stream().mapToInt(Pesanan::getTotal).sum();
    }

    public int getTotalBayarFromService() {
        return PesananService.getPesananList().stream()
                .filter(item -> item.getNo_meja().equals(no_meja))
                .collect(Collectors.toList()).stream()
                .mapToInt(Pesanan::getTotal)
                .sum();
    }

    String getId_transaksi() {
        return id_transaksi;
    }

    public String getNo_meja() {
        return no_meja;
    }

    public Date getTanggal() {
        return tanggal;
    }

    // Property
    public StringProperty no_mejaProperty() {
        return new SimpleStringProperty(no_meja);
    }

    public StringProperty totalProperty() {
        return new SimpleStringProperty(rupiah(getTotalBayarFromService()));
    }

    public StringProperty pukulProperty() {
        LocalTime t = new LocalTime(tanggal);
        return new SimpleStringProperty(String.format("%d:%d WIB", t.getHourOfDay(), t.getMinuteOfHour()));
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
