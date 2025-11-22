package com.example.catalogapi.communications;

import com.example.catalogapi.communications.dto.InboundEmailRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.ResponseBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import java.io.ByteArrayInputStream;
import java.util.Properties;

@Component
public class SesInboundProcessor {
    private static final Logger log = LoggerFactory.getLogger(SesInboundProcessor.class);

    private final CommunicationService service;
    private final S3Client s3Client;
    private final String bucket;

    public SesInboundProcessor(
            CommunicationService service,
            @Value("${aws.region:us-east-1}") String region,
            @Value("${aws.access-key:}") String accessKey,
            @Value("${aws.secret-key:}") String secretKey,
            @Value("${ses.inbound.bucket:}") String bucket
    ) {
        this.service = service;
        this.bucket = bucket;
        AwsCredentialsProvider creds = (accessKey != null && !accessKey.isBlank())
                ? StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey))
                : software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider.create();
        this.s3Client = S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(creds)
                .build();
    }

    public void process(String s3Key) {
        if (bucket == null || bucket.isBlank()) {
            log.warn("ses.inbound.bucket not set; cannot fetch inbound email {}", s3Key);
            return;
        }
        try {
            ResponseBytes<?> bytes = s3Client.getObjectAsBytes(GetObjectRequest.builder()
                    .bucket(bucket)
                    .key(s3Key)
                    .build());
            MimeMessage mime = new MimeMessage(Session.getDefaultInstance(new Properties()), new ByteArrayInputStream(bytes.asByteArray()));
            InboundEmailRequest req = new InboundEmailRequest(
                    mime.getFrom() != null && mime.getFrom().length > 0 ? mime.getFrom()[0].toString() : "",
                    mime.getRecipients(jakarta.mail.Message.RecipientType.TO) != null && mime.getRecipients(jakarta.mail.Message.RecipientType.TO).length > 0 ? mime.getRecipients(jakarta.mail.Message.RecipientType.TO)[0].toString() : "",
                    mime.getSubject(),
                    mime.getContent().toString(),
                    mime.getMessageID(),
                    mime.getHeader("In-Reply-To", null),
                    null
            );
            service.inboundEmail(req);
        } catch (Exception ex) {
            log.error("Failed to process inbound email s3Key={}", s3Key, ex);
        }
    }
}
