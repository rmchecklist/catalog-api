package com.example.catalogapi.communications;

import com.example.catalogapi.communications.dto.NewThreadRequest;
import com.example.catalogapi.communications.dto.ReplyRequest;
import com.example.catalogapi.communications.dto.ThreadResponse;
import com.example.catalogapi.communications.dto.QuoteRequest;
import com.example.catalogapi.communications.dto.InboundEmailRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/communications")
public class CommunicationController {

    private final CommunicationService service;
    private final String quoteRecipient;

    public CommunicationController(CommunicationService service,
                                   @Value("${quotes.to:}") String quoteRecipient) {
        this.service = service;
        this.quoteRecipient = quoteRecipient;
    }

    @GetMapping
    public List<ThreadResponse> all() {
        return service.listThreads();
    }

    @GetMapping("/{id}")
    public ThreadResponse get(@PathVariable UUID id) {
        return service.getThread(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ThreadResponse create(@Valid @RequestBody NewThreadRequest request) {
        return service.createThread(request);
    }

    @PostMapping("/{id}/reply")
    public ThreadResponse reply(@PathVariable UUID id, @Valid @RequestBody ReplyRequest request) {
        return service.reply(id, request);
    }

    @PostMapping("/quote")
    @ResponseStatus(HttpStatus.CREATED)
    public ThreadResponse quote(@Valid @RequestBody QuoteRequest request, @RequestHeader(value = "X-Internal-Recipient", required = false) String internalRecipient) {
        String target = (internalRecipient != null && !internalRecipient.isBlank()) ? internalRecipient : quoteRecipient;
        return service.createQuoteThread(request, target);
    }

    @PostMapping("/vendor")
    @ResponseStatus(HttpStatus.CREATED)
    public ThreadResponse vendor(@Valid @RequestBody VendorEmailRequest request) {
        return service.createVendorThread(request.subject(), request.from(), request.to(), request.body());
    }

    // Webhook for inbound parsing (e.g., from SES pipeline or manual)
    @PostMapping("/inbound")
    @ResponseStatus(HttpStatus.CREATED)
    public ThreadResponse inbound(@Valid @RequestBody InboundEmailRequest request) {
        return service.inboundEmail(request);
    }

    public record VendorEmailRequest(
            @NotBlank String subject,
            @NotBlank @Email String from,
            @NotBlank @Email String to,
            String body
    ) {}
}
