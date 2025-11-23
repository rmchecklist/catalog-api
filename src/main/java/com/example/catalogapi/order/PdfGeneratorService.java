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
        String invNumber = isQuote ? quote.invoiceNumber() : order.invoiceNumber();

        // Header
        PdfPTable header = new PdfPTable(2);
        header.setWidthPercentage(100);
        header.setSpacingAfter(10f);
        header.addCell(borderless(new Phrase("Ilan Foods", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16))));
        header.addCell(borderless(new Phrase(title + " " + invNumber, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16))));
        header.addCell(borderless(new Phrase("445 Hawks Creek Pkwy\nFort Mill SC\ncatalog.ilanfoods.com\n717-215-0206")));
        header.addCell(borderless(new Phrase("Date: " + java.time.LocalDate.now())));
        doc.add(header);

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
        table.addCell(headerCell("SKU"));
        table.addCell(headerCell("Product"));
        table.addCell(headerCell("Option"));
        table.addCell(headerCell("Qty"));

        var items = isQuote ? quote.items() : order.items();
        items.forEach(item -> {
            table.addCell(item.sku() != null ? item.sku() : "");
            table.addCell(item.productName());
            table.addCell(item.optionLabel());
            table.addCell(String.valueOf(item.quantity()));
        });
        doc.add(table);
        doc.add(new Paragraph(" "));

        doc.add(new Paragraph("View online: " + viewUrl));
        doc.add(new Paragraph(" "));

        Image qr = buildQr(id.toString() + "|" + viewUrl);
        if (qr != null) {
            qr.scalePercent(60);
            doc.add(qr);
        }

        doc.add(new Paragraph("Thank you for choosing Ilan Foods!", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));

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

    private PdfPCell borderless(Phrase phrase) {
        PdfPCell cell = new PdfPCell(phrase);
        cell.setBorder(Rectangle.NO_BORDER);
        return cell;
    }

    private Image buildQr(String payload) {
        try {
            BitMatrix matrix = new MultiFormatWriter().encode(payload, BarcodeFormat.QR_CODE, 220, 220);
            var out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(matrix, "png", out);
            return Image.getInstance(out.toByteArray());
        } catch (Exception e) {
            return null;
        }
    }
}
