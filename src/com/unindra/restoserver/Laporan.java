package com.unindra.restoserver;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;
import com.unindra.restoserver.models.Item;
import com.unindra.restoserver.models.Menu;
import com.unindra.restoserver.models.Transaksi;
import javafx.collections.FXCollections;
import org.joda.time.LocalDate;
import org.joda.time.YearMonth;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static com.unindra.restoserver.Rupiah.rupiah;
import static com.unindra.restoserver.models.Item.getItems;
import static com.unindra.restoserver.models.Menu.getMenus;
import static com.unindra.restoserver.models.Menu.menu;
import static com.unindra.restoserver.models.Transaksi.getTransaksiList;

public class Laporan {

    private static final String bold = "fonts/OpenSans-Bold.ttf";

    public static void harian() throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont(bold, true);
        LocalDate localDate = new LocalDate(new Date());
        String fileName = String.format("laporan-harian-%s.pdf", localDate.toString());
        List<Transaksi> transaksiList = getTransaksiList(localDate);

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(
                new Paragraph()
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMultipliedLeading(1)
                        .add(new Text("Laporan Harian\n").setFont(boldFont))
                        .add(localDate.toString()));

        Table detailTable = new Table(new UnitValue[]{
                new UnitValue(UnitValue.PERCENT, 50),
                new UnitValue(UnitValue.PERCENT, 50)}, true);

        detailTable.addCell(cellNoBorder("Total Transaksi:"));
        detailTable.addCell(cellNoBorder(String.valueOf(transaksiList.size())));
        detailTable.addCell(cellNoBorder("Pemasukan:"));
        detailTable.addCell(
                cellNoBorder(
                        rupiah(
                                transaksiList
                                        .stream()
                                        .mapToInt(Transaksi::getTotalBayarFromDB)
                                        .sum())));
        detailTable.addCell(cellNoBorder("Menu Favorit:"));
        detailTable.addCell(cellNoBorder(menu(localDate).getNama_menu()));

        document.add(detailTable);

        Table transaksiTable = new Table(3);
        transaksiTable.setWidth(520);

        transaksiTable.addHeaderCell(cell("Id Transaksi").setFont(boldFont));
        transaksiTable.addHeaderCell(cell("No Meja").setFont(boldFont));
        transaksiTable.addHeaderCell(cell("Total Bayar").setFont(boldFont));

        transaksiList.forEach(transaksi -> {
            transaksiTable.addCell(cell(String.valueOf(transaksi.getId_transaksi())));
            transaksiTable.addCell(cell(transaksi.getNo_meja()));
            transaksiTable.addCell(cell(rupiah(transaksi.getTotalBayarFromDB())));
        });

        document.add(transaksiTable.setMarginTop(10));
        document.close();
        showReport(fileName);
    }

    public static void bulanan() throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont(bold, true);
        LocalDate localDate = new LocalDate(new Date());
        String fileName = String.format("laporan-bulanan-%s.pdf", localDate.toString());

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(
                new Paragraph()
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMultipliedLeading(1)
                        .add(new Text("Laporan Bulanan\n").setFont(boldFont))
                        .add(localDate.toString()));

        Table transaksiTable = new Table(2);
        transaksiTable.setWidth(520);

        transaksiTable.addHeaderCell(cell("Bulan").setFont(boldFont));
        transaksiTable.addHeaderCell(cell("Total Pemasukan").setFont(boldFont));


        AtomicInteger forMinusMonths = new AtomicInteger(0);
        while (true) {
            YearMonth yearMonth = new YearMonth(localDate.minusMonths(forMinusMonths.get()));
            int totalPemasukan = getTransaksiList(yearMonth.getYear(), yearMonth.getMonthOfYear())
                    .stream()
                    .mapToInt(Transaksi::getTotalBayarFromDB)
                    .sum();
            if (totalPemasukan == 0) break;
            transaksiTable.addCell(cell(yearMonth.monthOfYear().getAsText() + " " + yearMonth.getYear()));
            transaksiTable.addCell(cell(rupiah(totalPemasukan)));
            forMinusMonths.getAndIncrement();
        }

        document.add(new Paragraph("Tabel Transaksi").setFont(boldFont).setMarginTop(10));
        document.add(transaksiTable);
        document.close();
        showReport(fileName);
    }

    public static void menuFavorit() throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont(bold, true);
        LocalDate localDate = new LocalDate(new Date());
        String fileName = String.format("laporan-menu-favorit-%s.pdf", localDate.toString());

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(
                new Paragraph()
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMultipliedLeading(1)
                        .add(new Text("Laporan Menu Favorit\n").setFont(boldFont))
                        .add(localDate.toString()));

        Table transaksiTable = new Table(4);
        transaksiTable.setWidth(520);

        transaksiTable.addHeaderCell(cell("Nama Menu").setFont(boldFont));
        transaksiTable.addHeaderCell(cell("Tipe").setFont(boldFont));
        transaksiTable.addHeaderCell(cell("Harga").setFont(boldFont));
        transaksiTable.addHeaderCell(cell("Total Dipesan").setFont(boldFont));

        List<Menu> menus = FXCollections.observableArrayList(getMenus());
        menus.sort((menu1, menu2) -> {
            List<Item> items1 = getItems(menu1);
            List<Item> items2 = getItems(menu2);
            return items2.size() - items1.size();
        });

        menus.forEach(menu -> {
            transaksiTable.addCell(cell(menu.getNama_menu()));
            transaksiTable.addCell(cell(menu.getTipe_menu()));
            transaksiTable.addCell(cell(rupiah(menu.getHarga_menu())));
            transaksiTable.addCell(cell(String.valueOf(getItems(menu).size())));
        });

        document.add(new Paragraph("Tabel Menu").setMarginTop(10).setFont(boldFont));
        document.add(transaksiTable);
        document.close();
        showReport(fileName);
    }

    public static void kunjungan() throws IOException {
        PdfFont boldFont = PdfFontFactory.createFont(bold, true);
        LocalDate localDate = new LocalDate(new Date());
        String fileName = String.format("laporan-kunjungan-%s.pdf", localDate.toString());

        PdfWriter writer = new PdfWriter(fileName);
        PdfDocument pdf = new PdfDocument(writer);
        Document document = new Document(pdf);

        document.add(
                new Paragraph()
                        .setTextAlignment(TextAlignment.RIGHT)
                        .setMultipliedLeading(1)
                        .add(new Text("Laporan Kunjungan\n").setFont(boldFont))
                        .add(localDate.toString()));

        Table transaksiTable = new Table(2);
        transaksiTable.setWidth(520);

        transaksiTable.addHeaderCell(cell("Bulan").setFont(boldFont));
        transaksiTable.addHeaderCell(cell("Total Kunjungan").setFont(boldFont));

        AtomicInteger forMinusMonths = new AtomicInteger(0);
        while (true) {
            YearMonth yearMonth = new YearMonth(localDate.minusMonths(forMinusMonths.get()));
            int totalKunjungan = getTransaksiList(yearMonth.getYear(), yearMonth.getMonthOfYear()).size();
            if (totalKunjungan == 0) break;
            transaksiTable.addCell(cell(yearMonth.monthOfYear().getAsText() + " " + yearMonth.getYear()));
            transaksiTable.addCell(cell(String.valueOf(totalKunjungan)));
            forMinusMonths.getAndIncrement();
        }

        document.add(new Paragraph("Tabel Kunjungan").setMarginTop(10).setFont(boldFont));
        document.add(transaksiTable);
        document.close();
        showReport(fileName);
    }

    private static Cell cellNoBorder(String text) {
        return new Cell()
                .setBorder(Border.NO_BORDER)
                .add(new Paragraph(text));
    }

    private static Cell cell(String text) {
        return new Cell().add(new Paragraph(text));
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
}
