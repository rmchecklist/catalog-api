package com.example.catalogapi.communications;

import com.example.catalogapi.communications.dto.InboundEmailRequest;
import com.example.catalogapi.communications.dto.NewThreadRequest;
import com.example.catalogapi.communications.dto.QuoteRequest;
import com.example.catalogapi.communications.dto.ReplyRequest;
import com.example.catalogapi.communications.dto.ThreadResponse;
import com.example.catalogapi.communications.CommunicationThreadType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class CommunicationService {

    private final CommunicationThreadRepository threadRepository;
    private final MessageRepository messageRepository;
    private final MailService mailService;

    public CommunicationService(CommunicationThreadRepository threadRepository, MessageRepository messageRepository, MailService mailService) {
        this.threadRepository = threadRepository;
        this.messageRepository = messageRepository;
        this.mailService = mailService;
    }

    public List<ThreadResponse> listThreads() {
        return threadRepository.findAll().stream()
                .sorted(Comparator.comparing(CommunicationThreadEntity::getUpdatedAt).reversed())
                .map(this::toResponse)
                .toList();
    }

    public ThreadResponse getThread(UUID id) {
        CommunicationThreadEntity thread = threadRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found: " + id));
        return toResponse(thread);
    }

    public ThreadResponse createThread(NewThreadRequest request) {
        CommunicationThreadEntity thread = new CommunicationThreadEntity();
        thread.setSubject(request.subject());
        thread.setType(parseType(request.type()));
        MessageEntity message = buildMessage(null, request.from(), request.to(), request.cc(), request.bcc(), request.body(), MessageDirection.OUTBOUND);
        message.setThread(thread);
        thread.getMessages().add(message);
        thread.setUpdatedAt(message.getCreatedAt());
        threadRepository.save(thread);
        mailService.sendText(request.to(), request.subject(), request.body(), request.from());
        return toResponse(thread);
    }

    public ThreadResponse createVendorThread(String subject, String from, String to, String body) {
        NewThreadRequest req = new NewThreadRequest(subject, from, to, null, null, body, "VENDOR");
        return createThread(req);
    }

    public ThreadResponse reply(UUID threadId, ReplyRequest request) {
        CommunicationThreadEntity thread = threadRepository.findById(threadId)
                .orElseThrow(() -> new IllegalArgumentException("Thread not found: " + threadId));
        MessageEntity message = buildMessage(thread, request.from(), request.to(), request.cc(), request.bcc(), request.body(), MessageDirection.OUTBOUND);
        thread.getMessages().add(message);
        if (request.status() != null) {
            thread.setStatus(request.status());
        }
        thread.setUpdatedAt(message.getCreatedAt());
        threadRepository.save(thread);
        mailService.sendText(request.to(), thread.getSubject(), request.body(), request.from());
        return toResponse(thread);
    }

    public ThreadResponse createQuoteThread(QuoteRequest request, String internalRecipient) {
        CommunicationThreadEntity thread = new CommunicationThreadEntity();
        thread.setSubject("Quote request from " + request.contact().name());
        thread.setType(CommunicationThreadType.CUSTOMER);
        String body = buildQuoteBody(request);
        MessageEntity message = buildMessage(null, request.contact().email(), internalRecipient, null, null, body, MessageDirection.INBOUND);
        message.setThread(thread);
        thread.getMessages().add(message);
        thread.setUpdatedAt(message.getCreatedAt());
        threadRepository.save(thread);
        mailService.sendText(internalRecipient, thread.getSubject(), body, request.contact().email());
        return toResponse(thread);
    }

    public ThreadResponse inboundEmail(InboundEmailRequest request) {
        CommunicationThreadEntity thread = resolveThread(request);
        MessageEntity message = buildMessage(thread, request.from(), request.to(), null, null, request.body(), MessageDirection.INBOUND);
        if (request.messageId() != null) {
            message.setMessageId(request.messageId());
        }
        if (request.inReplyTo() != null) {
            message.setInReplyTo(request.inReplyTo());
        }
        thread.getMessages().add(message);
        thread.setUpdatedAt(message.getCreatedAt());
        threadRepository.save(thread);
        return toResponse(thread);
    }

    private MessageEntity buildMessage(CommunicationThreadEntity thread, String from, String to, String cc, String bcc, String body, MessageDirection direction) {
        MessageEntity message = new MessageEntity();
        message.setThread(thread);
        message.setFromAddress(from);
        message.setToAddress(to);
        message.setCc(cc);
        message.setBcc(bcc);
        message.setBody(body);
        message.setDirection(direction);
        message.setMessageId("local-" + java.util.UUID.randomUUID());
        return message;
    }

    private String buildQuoteBody(QuoteRequest request) {
        StringBuilder sb = new StringBuilder();
        sb.append("Quote request from ").append(request.contact().name()).append("\n");
        sb.append("Email: ").append(request.contact().email()).append("\n");
        if (request.contact().instructions() != null && !request.contact().instructions().isBlank()) {
            sb.append("Instructions: ").append(request.contact().instructions()).append("\n");
        }
        sb.append("\nItems:\n");
        for (QuoteRequest.QuoteItem item : request.items()) {
            sb.append("- ").append(item.name())
                    .append(" | Option: ").append(item.option())
                    .append(" | Qty: ").append(item.quantity())
                    .append(" (min ").append(item.minQty()).append(")");
            if (!item.available()) {
                sb.append(" [unavailable]");
            }
            sb.append("\n");
        }
        return sb.toString();
    }

    private CommunicationThreadEntity resolveThread(InboundEmailRequest request) {
        if (request.threadId() != null && !request.threadId().isBlank()) {
            try {
                UUID id = UUID.fromString(request.threadId());
                return threadRepository.findById(id).orElseGet(() -> newThread(request.subject()));
            } catch (IllegalArgumentException ignored) {
            }
        }
        if (request.inReplyTo() != null && !request.inReplyTo().isBlank()) {
            Optional<MessageEntity> parent = messageRepository.findFirstByMessageId(request.inReplyTo());
            if (parent.isPresent()) {
                return parent.get().getThread();
            }
        }
        return threadRepository.findAll().stream()
                .filter(t -> request.subject().equalsIgnoreCase(t.getSubject()))
                .findFirst()
                .orElseGet(() -> newThread(request.subject()));
    }

    private CommunicationThreadEntity newThread(String subject) {
        CommunicationThreadEntity t = new CommunicationThreadEntity();
        t.setSubject(subject);
        t.setType(CommunicationThreadType.CUSTOMER);
        return t;
    }

    private CommunicationThreadType parseType(String raw) {
        if (raw == null || raw.isBlank()) return CommunicationThreadType.CUSTOMER;
        try {
            return CommunicationThreadType.valueOf(raw.toUpperCase());
        } catch (Exception ignored) {
            return CommunicationThreadType.CUSTOMER;
        }
    }

    private ThreadResponse toResponse(CommunicationThreadEntity thread) {
        List<ThreadResponse.MessageSummary> messages = thread.getMessages().stream()
                .sorted(Comparator.comparing(MessageEntity::getCreatedAt))
                .map(msg -> new ThreadResponse.MessageSummary(
                        msg.getId(),
                        msg.getFromAddress(),
                        msg.getToAddress(),
                        msg.getBody(),
                        msg.getDirection().name().toLowerCase(),
                        msg.getCreatedAt()
                ))
                .toList();
        return new ThreadResponse(
                thread.getId(),
                thread.getSubject(),
                thread.getStatus(),
                thread.getType(),
                thread.getUpdatedAt(),
                messages
        );
    }
}
