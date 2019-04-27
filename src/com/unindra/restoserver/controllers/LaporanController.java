package com.unindra.restoserver.controllers;

import com.unindra.restoserver.Laporan;
import com.unindra.restoserver.models.Menu;
import com.unindra.restoserver.models.Transaksi;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import static com.unindra.restoserver.Rupiah.rupiah;
import static com.unindra.restoserver.models.Item.getItems;
import static com.unindra.restoserver.models.Menu.getMenus;
import static com.unindra.restoserver.models.Menu.menu;
import static com.unindra.restoserver.models.Transaksi.getTransaksiList;

public class LaporanController implements Initializable {
    public BarChart barChart;
    public PieChart pieChart;
    public LineChart lineChart;
    public Label tglLabel;
    public Label totalTransaksiLabel;
    public Label pemasukanLabel;
    public Label menufavLabel;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        LocalDate localDate = new LocalDate(new Date());

        tglLabel.setText(localDate.toString());
        totalTransaksiLabel.setText(String.valueOf(getTransaksiList(localDate).size()));
        pemasukanLabel.setText(rupiah(getTransaksiList(localDate)
                .stream()
                .mapToInt(Transaksi::getTotalBayarFromDB)
                .sum()));
        menufavLabel.setText(menu(localDate).getNama_menu());

        XYChart.Series seriesBar = new XYChart.Series();

        for (AtomicInteger i = new AtomicInteger(5); i.get() >= 0; i.getAndDecrement()) {
            YearMonth yearMonth = new YearMonth(localDate.minusMonths(i.get()));
            String bulan = yearMonth.monthOfYear().getAsText() + " " + yearMonth.getYear();
            Integer totalPendapatan = getTransaksiList(
                    yearMonth.getYear(), yearMonth.getMonthOfYear())
                    .stream()
                    .mapToInt(Transaksi::getTotalBayarFromDB)
                    .sum();
            seriesBar.getData().add(new XYChart.Data(bulan, totalPendapatan));
        }

        barChart.getData().addAll(seriesBar);
        barChart.getXAxis().setLabel("Bulan");
        barChart.getYAxis().setLabel("Pemasukan (Rp)");

        ObservableList<PieChart.Data> pieObservableList = FXCollections.observableArrayList();
        for (Menu menu : getMenus())
            pieObservableList.add(new PieChart.Data(menu.getNama_menu(), getItems(menu).size()));

        pieChart.setData(pieObservableList);
        pieChart.setStartAngle(90);

        XYChart.Series seriesLine = new XYChart.Series();

        for (AtomicInteger i = new AtomicInteger(5); i.get() >= 0; i.getAndDecrement()) {
            YearMonth yearMonth = new YearMonth(localDate.minusMonths(i.get()));
            String bulan = yearMonth.monthOfYear().getAsText() + " " + yearMonth.getYear();
            Integer totalKunjungan = getTransaksiList(yearMonth.getYear(), yearMonth.getMonthOfYear()).size();
            seriesLine.getData().add(new XYChart.Data<>(bulan, totalKunjungan));
        }

        lineChart.getData().addAll(seriesLine);
        lineChart.getXAxis().setLabel("Bulan");
        lineChart.getYAxis().setLabel("Kunjungan");
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
