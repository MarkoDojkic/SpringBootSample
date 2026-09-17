package dev.markodojkic.api;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tika.Tika;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

@RestController
@RequestMapping("/api/demo")
public class DocumentDemoController {

    private final Tika tika = new Tika();

    @GetMapping("/document")
    public ResponseEntity<String> document(@RequestParam(defaultValue = "demo") String text) throws IOException {
        String textFromPdf = pdfText(text);
        return ResponseEntity.ok("text=" + text + "\npdfText=" + textFromPdf);
    }

    @GetMapping("/excel")
    public ResponseEntity<String> excel() throws IOException {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("demo");
            Row row = sheet.createRow(0);
            row.createCell(0).setCellValue("Spring Boot 3");
            row.createCell(1).setCellValue("Tika/POI/PDFBox/ZXing");

            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            workbook.write(baos);
            String base64 = Base64.getEncoder().encodeToString(baos.toByteArray());
            return ResponseEntity.ok("xlsx-base64-length=" + base64.length());
        }
    }

    @GetMapping("/qr")
    public ResponseEntity<byte[]> qr(@RequestParam(defaultValue = "spring-boot-3-demo") String payload) throws Exception {
        BitMatrix matrix = new MultiFormatWriter().encode(payload, BarcodeFormat.QR_CODE, 300, 300);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(matrix, "PNG", baos);
        return ResponseEntity.ok().contentType(MediaType.IMAGE_PNG).body(baos.toByteArray());
    }

    @GetMapping("/pdf")
    public ResponseEntity<byte[]> pdf(@RequestParam(defaultValue = "demo") String text) throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);
        document.add(new Paragraph("Spring Boot 3 demo"));
        document.add(new Paragraph(text));
        document.close();
        pdfDocument.close();
        return ResponseEntity.ok().contentType(MediaType.APPLICATION_PDF).body(baos.toByteArray());
    }

    private String pdfText(String text) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDocument = new PdfDocument(writer);
        Document document = new Document(pdfDocument);
        document.add(new Paragraph("Spring Boot 3 demo"));
        document.add(new Paragraph(text));
        document.close();
        pdfDocument.close();

        try (PDDocument pdDocument = Loader.loadPDF(baos.toByteArray())) {
            return new PDFTextStripper().getText(pdDocument);
        }
    }

    @GetMapping("/tika")
    public ResponseEntity<String> tika(@RequestParam(defaultValue = "hello") String text) throws Exception {
        byte[] content = text.getBytes(StandardCharsets.UTF_8);
        String detected = tika.detect(content);
        return ResponseEntity.ok("detected-type=" + detected);
    }
}
