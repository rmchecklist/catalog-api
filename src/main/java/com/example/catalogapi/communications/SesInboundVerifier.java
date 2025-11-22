package com.example.catalogapi.communications;

import org.springframework.stereotype.Component;

@Component
public class SesInboundVerifier {
    public void verifySource() {
        // TODO: verify SNS signature if using SNS/SQS pipeline
    }
}
