package dev.markodojkic.api;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/reports")
public class ReportController {
    @GetMapping(value = "/eligibility", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<byte[]> eligibility(
            @RequestParam(name = "patientId") String patientId) throws IOException, JRException {
        ClassPathResource template = new ClassPathResource("reports/eligibility.jrxml");
        JasperReport report;
        try (InputStream input = template.getInputStream()) {
            report = JasperCompileManager.compileReport(input);
        }
        JasperPrint print = JasperFillManager.fillReport(report, Map.of("patientId", patientId), new net.sf.jasperreports.engine.JREmptyDataSource());
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .body(JasperExportManager.exportReportToPdf(print));
    }
}
