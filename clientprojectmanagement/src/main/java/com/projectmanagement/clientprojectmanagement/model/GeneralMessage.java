package com.projectmanagement.clientprojectmanagement.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "general_messages")
public class GeneralMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String content;

    private LocalDateTime timestamp;

    // Who sent it
    @ManyToOne
    @JoinColumn(name = "sender_id")
    private User sender;

    // Which client this conversation belongs to
    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public User getSender() { return sender; }
    public void setSender(User sender) { this.sender = sender; }

    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }
}