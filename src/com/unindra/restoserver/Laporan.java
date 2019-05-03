package com.unindra.restoserver;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
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
import com.unindra.restoserver.models.ItemService;
import com.unindra.restoserver.models.Menu;
import com.unindra.restoserver.models.Transaksi;
import javafx.collections.FXCollections;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.YearMonth;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;

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
                                        .mapToInt(Transaksi::getTotalBayar)
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
            transaksiTable.addCell(cell(rupiah(transaksi.getTotalBayar())));
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

        for (int i = 0; i < 5; i++) {
            YearMonth yearMonth = new YearMonth(localDate.minusMonths(i));
            int totalPemasukan = getTransaksiList(yearMonth.getYear(), yearMonth.getMonthOfYear())
                    .stream()
                    .mapToInt(Transaksi::getTotalBayar)
                    .sum();
            if (totalPemasukan == 0) break;
            transaksiTable.addCell(cell(yearMonth.monthOfYear().getAsText() + " " + yearMonth.getYear()));
            transaksiTable.addCell(cell(rupiah(totalPemasukan)));
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

        for (int i = 0; i < 5; i++) {
            YearMonth yearMonth = new YearMonth(localDate.minusMonths(i));
            int totalKunjungan = getTransaksiList(yearMonth.getYear(), yearMonth.getMonthOfYear()).size();
            if (totalKunjungan == 0) break;
            transaksiTable.addCell(cell(yearMonth.monthOfYear().getAsText() + " " + yearMonth.getYear()));
            transaksiTable.addCell(cell(String.valueOf(totalKunjungan)));
        }

        document.add(new Paragraph("Tabel Kunjungan").setMarginTop(10).setFont(boldFont));
        document.add(transaksiTable);
        document.close();
        showReport(fileName);
    }

    public static void bill(Transaksi transaksi) throws IOException {
        List<Item> items = ItemService.getItems(transaksi);

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
                        .add(items.get(0).getNo_meja())
                        .add("\tTanggal:")
                        .add(localDate+" "+new LocalTime().toString().substring(0, 8))
                        .add("\n-----------------------------------------------------------------------------------------\n")
        );

        Table itemsTable = new Table(new UnitValue[]{
                new UnitValue(UnitValue.PERCENT, 30),
                new UnitValue(UnitValue.PERCENT, 20),
                new UnitValue(UnitValue.PERCENT, 50),}, true);

        itemsTable.setFontSize(6);
        itemsTable.setTextAlignment(TextAlignment.CENTER);

        items.forEach(item -> {
            itemsTable.addCell(cellNoBorder(menu(item).getNama_menu()));
            itemsTable.addCell(cellNoBorder(item.getJumlah_item()+"x"));
            itemsTable.addCell(cellNoBorder(rupiah(item.getTotal())));
        });

        document.add(itemsTable);

        document.add(
                new Paragraph()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(5)
                        .add("-----------------------------------------------------------------------------------------")
        );

        Table footerTable = new Table(new UnitValue[]{
                new UnitValue(UnitValue.PERCENT, 50),
                new UnitValue(UnitValue.PERCENT, 50),}, true);

        footerTable.setTextAlignment(TextAlignment.RIGHT);
        footerTable.addCell(cellNoBorder("Total").setFontSize(6));
        footerTable.addCell(cellNoBorder(rupiah(transaksi.getTotalBayar())).setFontSize(6));

        document.add(footerTable);
        document.close();
        showReport(fileName);
    }

    public static void struk(Transaksi transaksi, int tunai) throws IOException {
        List<Item> items = ItemService.getItems(transaksi);

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
                        .add(items.get(0).getNo_meja())
                        .add("\tTanggal:")
                        .add(localDate+" "+new LocalTime().toString().substring(0, 8))
                        .add("\n-----------------------------------------------------------------------------------------\n")
        );

        Table itemsTable = new Table(new UnitValue[]{
                new UnitValue(UnitValue.PERCENT, 30),
                new UnitValue(UnitValue.PERCENT, 20),
                new UnitValue(UnitValue.PERCENT, 50),}, true);

        itemsTable.setFontSize(6);
        itemsTable.setTextAlignment(TextAlignment.CENTER);

        items.forEach(item -> {
            itemsTable.addCell(cellNoBorder(menu(item).getNama_menu()));
            itemsTable.addCell(cellNoBorder(item.getJumlah_item()+"x"));
            itemsTable.addCell(cellNoBorder(rupiah(item.getTotal())));
        });

        document.add(itemsTable);

        document.add(
                new Paragraph()
                        .setTextAlignment(TextAlignment.CENTER)
                        .setFontSize(5)
                        .add("-----------------------------------------------------------------------------------------")
        );

        Table footerTable = new Table(new UnitValue[]{
                new UnitValue(UnitValue.PERCENT, 50),
                new UnitValue(UnitValue.PERCENT, 50),}, true);

        footerTable.setTextAlignment(TextAlignment.RIGHT);
        footerTable.addCell(cellNoBorder("Total").setFontSize(6));
        footerTable.addCell(cellNoBorder(rupiah(transaksi.getTotalBayar())).setFontSize(6));
        footerTable.addCell(cellNoBorder("Tunai").setFontSize(6));
        footerTable.addCell(cellNoBorder(rupiah(tunai)).setFontSize(6));
        footerTable.addCell(cellNoBorder("Kembali").setFontSize(6));
        footerTable.addCell(cellNoBorder(rupiah(tunai - transaksi.getTotalBayar())).setFontSize(6));

        document.add(footerTable);
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
