package com.example.catalogapi.communications;

import jakarta.persistence.*;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "comm_threads")
public class CommunicationThreadEntity {
    @Id
    @GeneratedValue
    private UUID id;

    private String subject;

    @Enumerated(EnumType.STRING)
    private CommunicationThreadStatus status = CommunicationThreadStatus.OPEN;

    @Enumerated(EnumType.STRING)
    private CommunicationThreadType type = CommunicationThreadType.CUSTOMER;

    private OffsetDateTime createdAt = OffsetDateTime.now();
    private OffsetDateTime updatedAt = OffsetDateTime.now();

    @OneToMany(mappedBy = "thread", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @OrderBy("createdAt ASC")
    private List<MessageEntity> messages = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public CommunicationThreadStatus getStatus() {
        return status;
    }

    public void setStatus(CommunicationThreadStatus status) {
        this.status = status;
    }

    public CommunicationThreadType getType() {
        return type;
    }

    public void setType(CommunicationThreadType type) {
        this.type = type;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public OffsetDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(OffsetDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<MessageEntity> getMessages() {
        return messages;
    }
}
