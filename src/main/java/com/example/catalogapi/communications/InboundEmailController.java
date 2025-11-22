package com.example.catalogapi.communications;

import com.example.catalogapi.communications.dto.InboundEmailRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/communications/inbound/ses")
public class InboundEmailController {

    private final CommunicationService service;
    private final SesInboundVerifier verifier;

    public InboundEmailController(CommunicationService service, SesInboundVerifier verifier) {
        this.service = service;
        this.verifier = verifier;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void receive(@Valid @RequestBody InboundEmailRequest request) {
        verifier.verifySource(); // placeholder hook
        service.inboundEmail(request);
    }
}
