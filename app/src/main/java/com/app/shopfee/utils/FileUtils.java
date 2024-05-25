package com.app.shopfee.utils;

import android.os.Environment;

import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.layout.property.UnitValue;

import org.apache.poi.xwpf.usermodel.ParagraphAlignment;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.apache.poi.xwpf.usermodel.XWPFParagraph;
import org.apache.poi.xwpf.usermodel.XWPFRun;
import org.apache.poi.xwpf.usermodel.XWPFTable;
import org.apache.poi.xwpf.usermodel.XWPFTableCell;
import org.apache.poi.xwpf.usermodel.XWPFTableRow;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class FileUtils {
    public static void createPDF(String filePath, String title, List<String[]> data) throws IOException {
        File file = new File(filePath);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);

        // Add title
        Paragraph titleParagraph = new Paragraph(sanitizeString(title))
                .setFontSize(18)
                .setBold()
                .setTextAlignment(TextAlignment.CENTER);
        document.add(titleParagraph);

        // Add space
        document.add(new Paragraph("\n"));

        // Add table
        float[] columnWidths = {2, 5, 3, 2};
        Table table = new Table(UnitValue.createPercentArray(columnWidths)).useAllAvailableWidth();

        // Add table header
        String[] headers = {"Ordre code", "Email", "Order date", "Total price"};
        for (String header : headers) {
            Cell headerCell = new Cell()
                    .add(new Paragraph(sanitizeString(header)).setBold())
                    .setBackgroundColor(DeviceGray.GRAY)
                    .setTextAlignment(TextAlignment.CENTER);
            table.addHeaderCell(headerCell);
        }

        // Add table rows
        for (String[] row : data) {
            for (String cellValue : row) {
                Cell cell = new Cell()
                        .add(new Paragraph(sanitizeString(cellValue)))
                        .setTextAlignment(TextAlignment.CENTER);
                table.addCell(cell);
            }
        }

        document.add(table);
        document.close();
    }

    public static void createWord(String filePath, String title, List<String[]> data) throws IOException {
        XWPFDocument document = new XWPFDocument();

        // Add title
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleRun = titleParagraph.createRun();
        titleRun.setBold(true);
        titleRun.setFontSize(18);
        titleRun.setText(title);

        // Add table
        XWPFTable table = document.createTable();

        // Add table header
        XWPFTableRow headerRow = table.getRow(0);
        String[] headers = {"Order code", "Email", "Order date", "Total price"};
        for (int i = 0; i < headers.length; i++) {
            XWPFTableCell cell = headerRow.getCell(i);
            if (cell == null) {
                cell = headerRow.addNewTableCell();
            }
            XWPFRun run = cell.getParagraphs().get(0).createRun();
            run.setBold(true);
            run.setText(headers[i]);
            cell.getCTTc().addNewTcPr().addNewTcW().setW(2000); // Set column width
        }

        // Add table rows
        for (String[] row : data) {
            XWPFTableRow tableRow = table.createRow();
            for (int i = 0; i < row.length; i++) {
                XWPFTableCell cell = tableRow.getCell(i);
                if (cell == null) {
                    cell = tableRow.addNewTableCell();
                }
                cell.setText(row[i]);
                cell.getCTTc().addNewTcPr().addNewTcW().setW(2000); // Set column width
            }
        }

        // Save the document
        try (FileOutputStream out = new FileOutputStream(new File(filePath))) {
            document.write(out);
        }
    }
    private static String sanitizeString(String input) {
        return input == null ? "" : input.trim().replaceAll("[^\\x20-\\x7E]", "");
    }
}
