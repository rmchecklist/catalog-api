package com.example.catalogapi.communications;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record S3Notification(Record[] Records) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Record(S3 s3) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record S3(Bucket bucket, Object object) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Bucket(String name) {}
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Object(String key) {}
}
