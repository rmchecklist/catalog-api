package com.example.catalogapi.communications;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/communications/inbound/sns")
public class SnsInboundController {
    private static final Logger log = LoggerFactory.getLogger(SnsInboundController.class);
    private final ObjectMapper mapper = new ObjectMapper();
    private final SesInboundProcessor processor;

    public SnsInboundController(SesInboundProcessor processor) {
        this.processor = processor;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.ACCEPTED)
    public void handleSns(@RequestBody String payload) {
        try {
            SesSnsMessage sns = mapper.readValue(payload, SesSnsMessage.class);
            if (sns.Message() != null) {
                S3Notification notification = mapper.readValue(sns.Message(), S3Notification.class);
                if (notification.Records() != null && notification.Records().length > 0) {
                    String key = notification.Records()[0].s3().object().key();
                    processor.process(key);
                }
            }
        } catch (Exception ex) {
            log.error("Failed to process SNS inbound", ex);
        }
    }
}
