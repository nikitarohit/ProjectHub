package com.projectmanagement.clientprojectmanagement.dto;

import java.time.LocalDateTime;

/**
 * DTO for GeneralMessage
 *
 * Why DTO?
 * - Entity (GeneralMessage) mein User object hai
 * - User mein password field hai
 * - Agar directly entity return karein → password frontend pe aa jaayega!
 * - DTO sirf zaroori fields return karta hai → safe & clean
 */
public class GeneralMessageDTO {

    private Long   id;
    private String content;
    private LocalDateTime timestamp;

    // Sender info — sirf naam aur ID, password nahi!
    private Long   senderId;
    private String senderName;

    // Client ID — whose conversation this belongs to
    private Long   clientId;

    // ── Constructor ───────────────────────────────────────────
    public GeneralMessageDTO(Long id, String content, LocalDateTime timestamp,
                             Long senderId, String senderName, Long clientId) {
        this.id          = id;
        this.content     = content;
        this.timestamp   = timestamp;
        this.senderId    = senderId;
        this.senderName  = senderName;
        this.clientId    = clientId;
    }

    // ── Getters ───────────────────────────────────────────────
    public Long          getId()         { return id; }
    public String        getContent()    { return content; }
    public LocalDateTime getTimestamp()  { return timestamp; }
    public Long          getSenderId()   { return senderId; }
    public String        getSenderName() { return senderName; }
    public Long          getClientId()   { return clientId; }

    // ── Setters ───────────────────────────────────────────────
    public void setId(Long id)                    { this.id = id; }
    public void setContent(String content)        { this.content = content; }
    public void setTimestamp(LocalDateTime t)     { this.timestamp = t; }
    public void setSenderId(Long senderId)        { this.senderId = senderId; }
    public void setSenderName(String senderName)  { this.senderName = senderName; }
    public void setClientId(Long clientId)        { this.clientId = clientId; }
}