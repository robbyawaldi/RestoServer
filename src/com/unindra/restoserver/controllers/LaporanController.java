package com.unindra.restoserver.controllers;

import com.unindra.restoserver.Laporan;
import com.unindra.restoserver.models.Menu;
import com.unindra.restoserver.models.Transaksi;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static com.unindra.restoserver.Rupiah.rupiah;
import static com.unindra.restoserver.models.Pesanan.getItems;
import static com.unindra.restoserver.models.Menu.getMenus;
import static com.unindra.restoserver.models.Menu.menu;
import static com.unindra.restoserver.models.Transaksi.getTotalBayar;
import static com.unindra.restoserver.models.Transaksi.getTransaksiList;

public class LaporanController implements Initializable {
    public AreaChart bulananChart;
    public PieChart menuFavChart;
    public AreaChart kunjunganChart;
    public Label tglLabel;
    public Label totalTransaksiLabel;
    public Label pemasukanLabel;
    public Label menufavLabel;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        getTransaksiList().addListener((ListChangeListener<Transaksi>) c -> {
            LocalDate localDate = new LocalDate(new Date());

            tglLabel.setText(localDate.toString());
            totalTransaksiLabel.setText(String.valueOf(getTransaksiList(localDate).size()));
            pemasukanLabel.setText(rupiah(getTransaksiList(localDate)
                    .stream()
                    .mapToInt(Transaksi::getTotalBayar)
                    .sum()));
            menufavLabel.setText(menu(localDate).getNama());

            XYChart.Series bulananData = new XYChart.Series();
            for (int i = 4; i >= 0; i--) {
                YearMonth yearMonth = new YearMonth(localDate.minusMonths(i));
                String bulan = yearMonth.monthOfYear().getAsText() + " " + yearMonth.getYear();
                int totalPendapatan = getTotalBayar(yearMonth.getYear(), yearMonth.getMonthOfYear());
                bulananData.getData().add(new XYChart.Data<>(bulan, totalPendapatan));
            }

            Platform.runLater(() -> bulananChart.getData().setAll(bulananData));
            bulananChart.getYAxis().setLabel("Pemasukan (Rp)");

            ObservableList<PieChart.Data> menuFavData = FXCollections.observableArrayList();
            for (Menu menu : getMenus())
                menuFavData.add(new PieChart.Data(menu.getNama(), getItems(menu).size()));

            Platform.runLater(() -> menuFavChart.setData(menuFavData));
            menuFavChart.setStartAngle(90);

            XYChart.Series kunjunganData = new XYChart.Series();

            for (int i = 4; i >= 0; i--) {
                YearMonth yearMonth = new YearMonth(localDate.minusMonths(i));
                String bulan = yearMonth.monthOfYear().getAsText() + " " + yearMonth.getYear();
                int totalKunjungan = getTransaksiList(yearMonth.getYear(), yearMonth.getMonthOfYear()).size();
                kunjunganData.getData().add(new XYChart.Data<>(bulan, totalKunjungan));
            }

            Platform.runLater(() -> kunjunganChart.getData().setAll(kunjunganData));
            kunjunganChart.getYAxis().setLabel("Kunjungan");
        });
    }

    public void cetakHarianHandle() {
        Thread thread = new Thread(() -> {
            try {
                Laporan.harian();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void cetakBulananHandle() {
        Thread thread = new Thread(() -> {
            try {
                Laporan.bulanan();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void cetakFavoritHandle() {
        Thread thread = new Thread(() -> {
            try {
                Laporan.menuFavorit();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

    public void cetakKunjunganHandle() {
        Thread thread = new Thread(() -> {
            try {
                Laporan.kunjungan();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
