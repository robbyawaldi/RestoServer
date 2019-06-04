package com.unindra.restoserver;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.*;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.property.VerticalAlignment;
import com.unindra.restoserver.models.Menu;
import com.unindra.restoserver.models.Pesanan;
import com.unindra.restoserver.models.PesananService;
import com.unindra.restoserver.models.Transaksi;
import javafx.collections.FXCollections;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.unindra.restoserver.Rupiah.rupiah;
import static com.unindra.restoserver.models.Menu.getMenus;
import static com.unindra.restoserver.models.Menu.menu;
import static com.unindra.restoserver.models.Pesanan.getPesanan;
import static com.unindra.restoserver.models.Transaksi.getTotalBayar;
import static com.unindra.restoserver.models.Transaksi.getTransaksiList;

public class Laporan {

    private static final String bold = "fonts/OpenSans-Bold.ttf";

    private static Table kop_surat(String judul) throws MalformedURLException {
        Image image = new Image(ImageDataFactory.create("resources/icons/logo-ramen-bulet-merah-copy50x50.png"));

        return new Table(new UnitValue[]{
                new UnitValue(UnitValue.PERCENT, 10),
                new UnitValue(UnitValue.PERCENT, 90)}, true)
                .setFontSize(12)
                .addCell(cellNoBorder(image.setAutoScale(true)))
                .addCell(
                        cellNoBorder("Osaka Ramen\n" + judul)
                                .setTextAlignment(TextAlignment.CENTER)
                                .setHorizontalAlignment(HorizontalAlignment.CENTER)
                                .setVerticalAlignment(VerticalAlignment.MIDDLE));
    }

    private static Table signature(LocalDate tgl) {
        return new Table(
                1)
                .setFontSize(10)
                .setWidth(200)
                .setHeight(80)
                .setMarginTop(50)
                .setHorizontalAlignment(HorizontalAlignment.RIGHT)
                .addCell(
                        cellNoBorder("Depok" + ", " +
                                hari().get(tgl.getDayOfWeek()) + ", " +
                                tgl.getDayOfMonth() + " " +
                                bulan().get(tgl.getMonthOfYear()) + " " +
                                tgl.getYear())
                                .setTextAlignment(TextAlignment.CENTER))
                .addCell(
                        cellNoBorder("Pemilik\nTaufiq")
                                .setTextAlignment(TextAlignment.CENTER)
                                .setVerticalAlignment(VerticalAlignment.BOTTOM));
    }

    private static Cell cellNoBorder(String text) {
        return new Cell()
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph(text));
    }

    private static Cell cellNoBorder(Image image) {
        return new Cell()
                .setBorder(Border.NO_BORDER)
                .add(image);
    }

    private static Cell cell(String text) {
        return new Cell()
                .add(new Paragraph(text));
    }

    private static List<String> hari() {
        return Arrays.asList(
                "",
                "Senin",
                "Selasa",
                "Rabu",
                "Kamis",
                "Jumat",
                "Sabtu",
                "Minggu"
        );
    }

    private static List<String> bulan() {
        return Arrays.asList(
                "",
                "Januari",
                "Februari",
                "Maret",
                "April",
                "Mei",
                "Juni",
                "Juli",
                "Agustus",
                "September",
                "Oktober",
                "November",
                "Desember"
        );
    }

    private static void showReport(String fileName) {
        File file = new File(fileName);
        Desktop desktop = Desktop.getDesktop();
        try {
            desktop.open(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void pemesanan(LocalDate dari, LocalDate sampai) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont(bold, true);
        LocalDate localDate = new LocalDate(new Date());
        String fileName = String.format("laporan-pemesanan-%s.pdf", localDate.toString());

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        document.add(kop_surat("Laporan Pemesanan"));

        Table table = new Table(
                6)
                .setWidth(520)
                .setMarginTop(10)
                .setFontSize(10)
                .addHeaderCell(cell("Pukul").setFont(boldFont))
                .addHeaderCell(cell("No Meja").setFont(boldFont))
                .addHeaderCell(cell("Nama Menu").setFont(boldFont))
                .addHeaderCell(cell("Jumlah").setFont(boldFont))
                .addHeaderCell(cell("Harga").setFont(boldFont))
                .addHeaderCell(cell("Total Harga").setFont(boldFont));

        while (dari.isBefore(sampai.plusDays(1))) {
            getTransaksiList(dari).forEach(transaksi -> Pesanan.getPesanan(transaksi).forEach(pesanan -> {
                LocalTime t = new LocalTime(transaksi.getTanggal());
                table.addCell(cell(String.format("%d:%d WIB", t.getHourOfDay(), t.getMinuteOfHour())));
                table.addCell(cell(transaksi.getNo_meja()));
                table.addCell(cell(pesanan.getNama_menu()));
                table.addCell(cell(String.valueOf(pesanan.getJumlah())));
                table.addCell(cell(rupiah(menu(pesanan).getHarga_menu())));
                table.addCell(cell(rupiah(pesanan.getTotal())));
            }));
            dari = dari.plusDays(1);
        }

        document.add(table);
        document.add(signature(localDate));
        document.close();
        showReport(fileName);
    }

    public static void pemasukan(LocalDate dari, LocalDate sampai) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont(bold, true);
        LocalDate localDate = new LocalDate(new Date());
        String fileName = String.format("laporan-pemasukan-%s.pdf", localDate.toString());

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        document.add(kop_surat("Laporan Pemasukan"));

        Table table = new Table(
                2)
                .setWidth(520)
                .setMarginTop(10)
                .setFontSize(10)
                .addHeaderCell(cell("Tanggal").setFont(boldFont))
                .addHeaderCell(cell("Total Pemasukan").setFont(boldFont));

        while (dari.isBefore(sampai.plusDays(1))) {
            table.addCell(cell(dari.toString()));
            table.addCell(cell(rupiah(getTotalBayar(dari.getYear(), dari.getMonthOfYear()))));
            dari = dari.plusDays(1);
        }

        document.add(table);
        document.add(signature(localDate));
        document.close();
        showReport(fileName);
    }

    public static void menuFavorit(LocalDate dari, LocalDate sampai) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont(bold, true);
        LocalDate localDate = new LocalDate(new Date());
        String fileName = String.format("laporan-menu-favorit-%s.pdf", localDate.toString());

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        document.add(kop_surat("Laporan Menu Favorit"));

        Table table = new Table(
                4)
                .setWidth(520)
                .setMarginTop(10)
                .setFontSize(10)
                .addHeaderCell(cell("Nama Menu").setFont(boldFont))
                .addHeaderCell(cell("Tipe").setFont(boldFont))
                .addHeaderCell(cell("Harga").setFont(boldFont))
                .addHeaderCell(cell("Total Dipesan").setFont(boldFont));

        List<Menu> menus = FXCollections.observableArrayList(getMenus());
        menus.sort((menu1, menu2) -> {
            List<Pesanan> items1 = getPesanan(menu1);
            List<Pesanan> items2 = getPesanan(menu2);
            return items2.size() - items1.size();
        });
        for (Menu menu : getMenus()) {
            AtomicInteger jumlahMenu = new AtomicInteger();
            LocalDate tgl = dari;
            while (tgl.isBefore(sampai.plusDays(1))) {
                for (Transaksi transaksi : getTransaksiList(tgl)) {
                    jumlahMenu.addAndGet(getPesanan(transaksi, menu).stream().mapToInt(Pesanan::getJumlah).sum());
                }
                tgl = tgl.plusDays(1);
            }
            if (jumlahMenu.get() > 0) {
                table.addCell(cell(menu.getNama_menu()));
                table.addCell(cell(menu.getTipe()));
                table.addCell(cell(rupiah(menu.getHarga_menu())));
                table.addCell(cell(String.valueOf(getPesanan(menu).size())));
            }
        }

        document.add(table);
        document.add(signature(localDate));
        document.close();
        showReport(fileName);
    }

    public static void kunjungan(LocalDate dari, LocalDate sampai) throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont(bold, true);
        LocalDate localDate = new LocalDate(new Date());
        String fileName = String.format("laporan-kunjungan-%s.pdf", localDate.toString());

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, PageSize.A4);

        document.add(kop_surat("Laporan Kunjungan"));

        Table table = new Table(
                2)
                .setWidth(520)
                .setMarginTop(10)
                .setFontSize(10)
                .addHeaderCell(cell("Tanggal").setFont(boldFont))
                .addHeaderCell(cell("Total Kunjungan").setFont(boldFont));

        while (dari.isBefore(sampai.plusDays(1))) {
            int totalKunjungan = getTransaksiList(dari.getYear(), dari.getMonthOfYear()).size();
            table.addCell(cell(dari.toString()));
            table.addCell(cell(String.valueOf(totalKunjungan)));
            dari = dari.plusDays(1);
        }

        document.add(table);
        document.add(signature(localDate));
        document.close();
        showReport(fileName);
    }

    public static void bill(Transaksi transaksi) throws IOException {
        List<Pesanan> pesanans = PesananService.getItems(transaksi);

        String fileName = "bill.pdf";
        PdfFont boldFont = PdfFontFactory.createFont(bold, true);
        LocalDate localDate = new LocalDate(new Date());

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, new PageSize(new Rectangle(226.8f, 600f)));

        document.add(
                new Paragraph()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(5)
                        .add(new Text("OSAKA RAMEN").setFont(boldFont))
                        .add("\n-----------------------------------------------------------------------------------------\n")
                        .add("No Meja:")
                        .add(pesanans.get(0).getNo_meja())
                        .add("\tTanggal:")
                        .add(localDate + " " + new LocalTime().toString().substring(0, 8))
                        .add("\n-----------------------------------------------------------------------------------------\n")
        );

        Table table = new Table(new UnitValue[]{
                new UnitValue(UnitValue.PERCENT, 30),
                new UnitValue(UnitValue.PERCENT, 20),
                new UnitValue(UnitValue.PERCENT, 50),}, true)
                .setFontSize(6)
                .setTextAlignment(TextAlignment.CENTER);

        pesanans.forEach(item -> {
            table.addCell(cellNoBorder(menu(item).getNama_menu()));
            table.addCell(cellNoBorder(item.getJumlah() + "x"));
            table.addCell(cellNoBorder(rupiah(item.getTotal())));
        });

        document.add(table);

        document.add(
                new Paragraph()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(5)
                        .add("-----------------------------------------------------------------------------------------")
        );

        Table footerTable = new Table(new UnitValue[]{
                new UnitValue(UnitValue.PERCENT, 50),
                new UnitValue(UnitValue.PERCENT, 50),}, true)
                .setTextAlignment(TextAlignment.RIGHT)
                .addCell(cellNoBorder("Total").setFontSize(6))
                .addCell(cellNoBorder(rupiah(transaksi.getTotalBayarFromService())).setFontSize(6));

        document.add(footerTable);
        document.close();
        showReport(fileName);
    }

    public static void struk(Transaksi transaksi, int tunai) throws IOException {
        List<Pesanan> pesanans = PesananService.getItems(transaksi);

        String fileName = "struk.pdf";
        PdfFont boldFont = PdfFontFactory.createFont(bold, true);
        LocalDate localDate = new LocalDate(new Date());

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf, new PageSize(new Rectangle(226.8f, 600f)));

        document.add(
                new Paragraph()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(5)
                        .add(new Text("OSAKA RAMEN").setFont(boldFont))
                        .add("\nJl. Keadilan No. 23G, Rangkapan Jaya Baru, Pancoran Mas, Kota Depok Jawa Barat")
                        .add("\n-----------------------------------------------------------------------------------------\n")
                        .add("No Meja:")
                        .add(pesanans.get(0).getNo_meja())
                        .add("\tTanggal:")
                        .add(localDate + " " + new LocalTime().toString().substring(0, 8))
                        .add("\n-----------------------------------------------------------------------------------------\n")
        );

        Table table = new Table(new UnitValue[]{
                new UnitValue(UnitValue.PERCENT, 30),
                new UnitValue(UnitValue.PERCENT, 20),
                new UnitValue(UnitValue.PERCENT, 50),}, true)
                .setFontSize(6)
                .setTextAlignment(TextAlignment.CENTER);

        pesanans.forEach(item -> {
            table.addCell(cellNoBorder(menu(item).getNama_menu()));
            table.addCell(cellNoBorder(item.getJumlah() + "x"));
            table.addCell(cellNoBorder(rupiah(item.getTotal())));
        });

        document.add(table);

        document.add(
                new Paragraph()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(5)
                        .add("-----------------------------------------------------------------------------------------")
        );

        Table footerTable = new Table(new UnitValue[]{
                new UnitValue(UnitValue.PERCENT, 50),
                new UnitValue(UnitValue.PERCENT, 50),}, true)
                .setTextAlignment(TextAlignment.RIGHT)
                .addCell(cellNoBorder("Total").setFontSize(6))
                .addCell(cellNoBorder(rupiah(transaksi.getTotalBayarFromService())).setFontSize(6))
                .addCell(cellNoBorder("Tunai").setFontSize(6))
                .addCell(cellNoBorder(rupiah(tunai)).setFontSize(6))
                .addCell(cellNoBorder("Kembali").setFontSize(6))
                .addCell(cellNoBorder(rupiah(tunai - transaksi.getTotalBayarFromService())).setFontSize(6));

        document.add(footerTable);
        document.close();
        showReport(fileName);
    }
}
