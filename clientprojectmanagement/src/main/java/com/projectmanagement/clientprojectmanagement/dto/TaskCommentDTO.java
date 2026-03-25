package com.projectmanagement.clientprojectmanagement.dto;

import com.projectmanagement.clientprojectmanagement.model.TaskComment;
import java.time.LocalDateTime;

public class TaskCommentDTO {

    private Long id;
    private String content;
    private Long taskId;
    private Long senderId;
    private String senderName;
    private String senderRole;
    private LocalDateTime createdAt;

    public static TaskCommentDTO from(TaskComment c) {
        TaskCommentDTO dto = new TaskCommentDTO();
        dto.id        = c.getId();
        dto.content   = c.getContent();
        dto.createdAt = c.getCreatedAt();

        if (c.getTask() != null) {
            dto.taskId = c.getTask().getId();
        }
        if (c.getSender() != null) {
            dto.senderId   = c.getSender().getId();
            dto.senderName = c.getSender().getName();
            dto.senderRole = c.getSender().getRole();
        }
        return dto;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Long getTaskId() { return taskId; }
    public void setTaskId(Long taskId) { this.taskId = taskId; }

    public Long getSenderId() { return senderId; }
    public void setSenderId(Long senderId) { this.senderId = senderId; }

    public String getSenderName() { return senderName; }
    public void setSenderName(String senderName) { this.senderName = senderName; }

    public String getSenderRole() { return senderRole; }
    public void setSenderRole(String senderRole) { this.senderRole = senderRole; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}