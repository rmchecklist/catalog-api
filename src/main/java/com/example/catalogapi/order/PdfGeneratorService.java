package com.example.catalogapi.order;

import com.example.catalogapi.storage.SupabaseS3StorageService;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Service
public class PdfGeneratorService {

    private final SupabaseS3StorageService storageService;

    public PdfGeneratorService(SupabaseS3StorageService storageService) {
        this.storageService = storageService;
    }

    public PdfRenderResult renderAndStoreOrderPdf(OrderResponse order, String viewUrl) {
        byte[] pdf = render(order.id(), false, order, null, viewUrl);
        String key = storageService.uploadBytes("invoices", pdf, "application/pdf");
        return new PdfRenderResult(key, storageService.getPublicReadUrl(key));
    }

    public PdfRenderResult renderAndStoreQuotePdf(QuoteResponse quote, String viewUrl) {
        byte[] pdf = render(quote.id(), true, null, quote, viewUrl);
        String key = storageService.uploadBytes("quotes", pdf, "application/pdf");
        return new PdfRenderResult(key, storageService.getPublicReadUrl(key));
    }

    private byte[] render(UUID id, boolean isQuote, OrderResponse order, QuoteResponse quote, String viewUrl) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document doc = new Document();
            PdfWriter.getInstance(doc, baos);
            doc.open();

            String title = isQuote ? "Quote" : "Invoice";
            doc.add(new Paragraph(title + " #" + id.toString(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
            doc.add(new Paragraph(" "));

            String name = isQuote ? quote.name() : order.name();
            String email = isQuote ? quote.email() : order.email();
            String phone = isQuote ? quote.phone() : order.phone();
            String company = isQuote ? quote.company() : order.company();

            doc.add(new Paragraph("To: " + (name != null ? name : "Customer")));
            doc.add(new Paragraph("Email: " + email));
            if (phone != null && !phone.isBlank()) doc.add(new Paragraph("Phone: " + phone));
            if (company != null && !company.isBlank()) doc.add(new Paragraph("Company: " + company));
            doc.add(new Paragraph(" "));

            PdfPTable table = new PdfPTable(4);
            table.setWidthPercentage(100);
            table.addCell(headerCell("Product"));
            table.addCell(headerCell("Option"));
            table.addCell(headerCell("Qty"));
            table.addCell(headerCell("SKU"));

            var items = isQuote ? quote.items() : order.items();
            items.forEach(item -> {
                table.addCell(item.productName());
                table.addCell(item.optionLabel());
                table.addCell(String.valueOf(item.quantity()));
                table.addCell(item.sku() != null ? item.sku() : "");
            });
            doc.add(table);
            doc.add(new Paragraph(" "));

            doc.add(new Paragraph("View online: " + viewUrl));
            doc.add(new Paragraph(" "));

            Image barcode = buildBarcode(id.toString() + "|" + viewUrl);
            if (barcode != null) {
                barcode.scalePercent(60);
                doc.add(barcode);
            }

            doc.close();
            return baos.toByteArray();
        } catch (Exception ex) {
            throw new IllegalStateException("Failed to render PDF", ex);
        }
    }

    private PdfPCell headerCell(String text) {
        PdfPCell cell = new PdfPCell(new Phrase(text, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 11)));
        cell.setBackgroundColor(new Color(240, 240, 240));
        return cell;
    }

    private Image buildBarcode(String payload) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(payload, BarcodeFormat.CODE_128, 400, 80);
            var out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "png", out);
            return Image.getInstance(out.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }
}
