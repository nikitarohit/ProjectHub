package com.projectmanagement.clientprojectmanagement.dto;

import java.time.LocalDateTime;

public class MessageDTO {

    private String content;
    private String  senderName;
    private LocalDateTime timestamp;

    public MessageDTO(String content, String senderName, LocalDateTime timestamp) {
        this.content = content;
        this.senderName = senderName;
        this.timestamp = timestamp;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }
}
