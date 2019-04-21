package com.unindra.restoserver.controllers;

import com.unindra.restoserver.models.Transaksi;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

import static com.unindra.restoserver.models.Transaksi.getTransaksiList;

public class LaporanController implements Initializable {
    public BarChart barChart;

    @SuppressWarnings("unchecked")
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        barChart.getXAxis().setLabel("Bulan");
        barChart.getYAxis().setLabel("Pendapatan (Rp)");
        LocalDate localDate = new LocalDate(new Date());
        XYChart.Series series = new XYChart.Series();
        for (int i = 5; i > 0; i--) {
            YearMonth yearMonth = new YearMonth(localDate.minusMonths(i));
            String bulan = yearMonth.monthOfYear().getAsText() + " " + yearMonth.getYear();
            Integer total = getTransaksiList(yearMonth.getYear(), yearMonth.getMonthOfYear())
                    .stream()
                    .mapToInt(Transaksi::getTotalHargaFromDB)
                    .sum();
            series.getData().add(new XYChart.Data(bulan, total));
        }
        barChart.getData().addAll(series);
    }
}
