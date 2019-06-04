package com.unindra.restoserver.controllers;

import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXDatePicker;
import com.jfoenix.controls.JFXTreeTableView;
import com.jfoenix.controls.RecursiveTreeItem;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import com.unindra.restoserver.Laporan;
import com.unindra.restoserver.models.Menu;
import com.unindra.restoserver.models.Pesanan;
import com.unindra.restoserver.models.Transaksi;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.control.TreeTableView;
import javafx.util.Callback;
import org.joda.time.LocalDate;

import java.io.IOException;
import java.net.URL;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicInteger;

import static com.unindra.restoserver.models.Menu.getMenus;
import static com.unindra.restoserver.models.Menu.menu;
import static com.unindra.restoserver.models.Pesanan.getPesanan;
import static com.unindra.restoserver.models.Transaksi.getTransaksi;
import static com.unindra.restoserver.models.Transaksi.getTransaksiList;

public class LaporanController implements Initializable {
    public AreaChart pemasukanChart;
    public PieChart menuFavChart;
    public AreaChart kunjunganChart;
    public JFXComboBox<String> pilihLaporanCombo;
    public JFXDatePicker dariDatePicker;
    public JFXDatePicker sampaiDatePicker;
    public JFXTreeTableView<Pesanan> pemesananTableView;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        TreeTableColumn<Pesanan, String> pukulCol = new TreeTableColumn<>("Pukul");
        TreeTableColumn<Pesanan, String> mejaCol = new TreeTableColumn<>("No Meja");
        TreeTableColumn<Pesanan, String> namaCol = new TreeTableColumn<>("Nama");
        TreeTableColumn<Pesanan, Integer> jumlahCol = new TreeTableColumn<>("Jumlah");
        TreeTableColumn<Pesanan, String> hargaCol = new TreeTableColumn<>("Harga");
        TreeTableColumn<Pesanan, String> totalHargaCol = new TreeTableColumn<>("Total Harga");

        pukulCol.setCellValueFactory(param -> getTransaksi(param.getValue().getValue()).pukulProperty());
        pukulCol.setCellValueFactory(param -> getTransaksi(param.getValue().getValue()).pukulProperty());
        mejaCol.setCellValueFactory(param -> getTransaksi(param.getValue().getValue()).no_mejaProperty());
        namaCol.setCellValueFactory(param -> menu(param.getValue().getValue()).nama_menuProperty());
        jumlahCol.setCellValueFactory(param -> param.getValue().getValue().jumlahProperty());
        hargaCol.setCellValueFactory(param -> menu(param.getValue().getValue()).harga_menuProperty());
        totalHargaCol.setCellValueFactory(param -> param.getValue().getValue().totalHargaProperty());

        namaCol.setCellFactory(new Callback<TreeTableColumn<Pesanan, String>, TreeTableCell<Pesanan, String>>() {
            @Override
            public TreeTableCell<Pesanan, String> call(TreeTableColumn<Pesanan, String> param) {
                return new TreeTableCell<Pesanan, String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item == null) {
                            setText(null);
                        } else {
                            Pesanan i = Pesanan.getPesananList().get(getIndex());
                            if (menu(i).getTipe().equals("ramen"))
                                setText(item + " lv." + i.getLevel());
                            else setText(item);
                        }
                    }
                };
            }
        });

        TreeItem<Pesanan> rootItem = new RecursiveTreeItem<>(Pesanan.getPesananList(), RecursiveTreeObject::getChildren);
        pemesananTableView.setRoot(rootItem);
        pemesananTableView.getColumns().add(pukulCol);
        pemesananTableView.getColumns().add(mejaCol);
        pemesananTableView.getColumns().add(namaCol);
        pemesananTableView.getColumns().add(jumlahCol);
        pemesananTableView.getColumns().add(hargaCol);
        pemesananTableView.getColumns().add(totalHargaCol);
        pemesananTableView.setColumnResizePolicy(TreeTableView.CONSTRAINED_RESIZE_POLICY);

        Platform.runLater(() -> {
            pemasukanChart.getYAxis().setLabel("Pemasukan (Rp)");
            menuFavChart.setStartAngle(90);
            kunjunganChart.getYAxis().setLabel("Kunjungan");
        });

        ObservableList<String> pilihLaporanObservableList = FXCollections.observableArrayList(
                "Semua",
                "Pemesanan",
                "Menu Favorit",
                "Pemasukan",
                "Kunjungan");

        pilihLaporanCombo.setItems(pilihLaporanObservableList);
        pilihLaporanCombo.getSelectionModel().select(0);

        getTransaksiList().addListener(transaksiListChangeListener());
    }

    private void pemesanan(LocalDate dari, LocalDate sampai) {
        ObservableList<Pesanan> pesananList = FXCollections.observableArrayList();
        while (dari.isBefore(sampai.plusDays(1))) {
            getTransaksiList(dari).forEach(transaksi -> pesananList.addAll(getPesanan(transaksi)));
            dari = dari.plusDays(1);
        }
        Pesanan.getPesananList().setAll(pesananList);
    }

    @SuppressWarnings("unchecked")
    private void pemasukan(LocalDate dari, LocalDate sampai) {
        XYChart.Series data = new XYChart.Series();
        while (dari.isBefore(sampai.plusDays(1))) {
            int totalPemasukan = getTransaksiList(dari).stream().mapToInt(Transaksi::getTotalBayar).sum();
            LocalDate tgl = dari;
            Platform.runLater(() -> data.getData().add(
                    new XYChart.Data(tgl.getDayOfMonth() + " " + tgl.monthOfYear().getAsText(), totalPemasukan)
            ));
            dari = dari.plusDays(1);
        }
        Platform.runLater(() -> pemasukanChart.getData().setAll(data));
    }

    private void menuFavorit(LocalDate dari, LocalDate sampai) {
        ObservableList<PieChart.Data> menuFavData = FXCollections.observableArrayList();
        ObservableList<Menu> menus = getMenus();
        for (Menu menu : menus) {
            AtomicInteger jumlahMenu = new AtomicInteger();
            LocalDate tgl = dari;
            while (tgl.isBefore(sampai.plusDays(1))) {
                for (Transaksi transaksi : getTransaksiList(tgl)) {
                    jumlahMenu.addAndGet(getPesanan(transaksi, menu).stream().mapToInt(Pesanan::getJumlah).sum());
                }
                tgl = tgl.plusDays(1);
            }
            if (jumlahMenu.get() > 0)
                Platform.runLater(() -> menuFavData.add(new PieChart.Data(menu.getNama_menu(), jumlahMenu.get())));
        }
        Platform.runLater(() -> menuFavChart.setData(menuFavData));
    }

    @SuppressWarnings("unchecked")
    private void kunjungan(LocalDate dari, LocalDate sampai) {
        XYChart.Series data = new XYChart.Series();
        while (dari.isBefore(sampai.plusDays(1))) {
            int totalKunjungan = getTransaksiList(dari).size();
            LocalDate tgl = dari;
            Platform.runLater(() -> data.getData().add(
                    new XYChart.Data(tgl.getDayOfMonth() + " " + tgl.monthOfYear().getAsText(), totalKunjungan)
            ));
            dari = dari.plusDays(1);
        }
        Platform.runLater(() -> kunjunganChart.getData().setAll(data));
    }

    private ListChangeListener<Transaksi> transaksiListChangeListener() {
        return c -> {
            pemesanan(getDariDate(), getSampaiDate());
            pemasukan(getDariDate(), getSampaiDate());
            menuFavorit(getDariDate(), getSampaiDate());
            kunjungan(getDariDate(), getSampaiDate());
        };
    }

    private LocalDate getDariDate() {
        if (dariDatePicker.getValue() == null) return new LocalDate().minusMonths(1);
        else {
            Date dari = Date.from(dariDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            return new LocalDate(dari);
        }
    }

    private LocalDate getSampaiDate() {
        if (sampaiDatePicker.getValue() == null) return new LocalDate();
        else {
            Date sampai = Date.from(sampaiDatePicker.getValue().atStartOfDay(ZoneId.systemDefault()).toInstant());
            return new LocalDate(sampai);
        }
    }

    public void cetakHarianHandle() {
        Thread thread = new Thread(() -> {
            try {
                switch (pilihLaporanCombo.getSelectionModel().getSelectedItem()) {
                    case "Semua":
                        Laporan.pemesanan(getDariDate(), getSampaiDate());
                        Laporan.pemasukan(getDariDate(), getSampaiDate());
                        Laporan.menuFavorit(getDariDate(), getSampaiDate());
                        Laporan.kunjungan(getDariDate(), getSampaiDate());
                        break;
                    case "Pemesanan":
                        Laporan.pemesanan(getDariDate(), getSampaiDate());
                        break;
                    case "Menu Favorit":
                        Laporan.menuFavorit(getDariDate(), getSampaiDate());
                        break;
                    case "Pemasukan":
                        Laporan.pemasukan(getDariDate(), getSampaiDate());
                        break;
                    case "Kunjungan":
                        Laporan.kunjungan(getDariDate(), getSampaiDate());
                        break;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }
}
