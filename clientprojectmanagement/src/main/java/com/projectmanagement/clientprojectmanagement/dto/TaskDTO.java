package com.projectmanagement.clientprojectmanagement.dto;

import com.projectmanagement.clientprojectmanagement.model.Task;
import java.time.LocalDateTime;

public class TaskDTO {

    private Long id;
    private String title;
    private String description;
    private String status;
    private String priority;
    private Long projectId;
    private String projectTitle;
    private Long assignedToId;
    private String assignedToName;
    private Long createdById;
    private String createdByName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // ── Static factory ─────────────────────────────────────────────────────

    public static TaskDTO from(Task t) {
        TaskDTO dto = new TaskDTO();
        dto.id          = t.getId();
        dto.title       = t.getTitle();
        dto.description = t.getDescription();
        dto.status      = t.getStatus();
        dto.priority    = t.getPriority();
        dto.createdAt   = t.getCreatedAt();
        dto.updatedAt   = t.getUpdatedAt();

        if (t.getProject() != null) {
            dto.projectId    = t.getProject().getId();
            dto.projectTitle = t.getProject().getTitle();
        }
        if (t.getAssignedTo() != null) {
            dto.assignedToId   = t.getAssignedTo().getId();
            dto.assignedToName = t.getAssignedTo().getName();
        }
        if (t.getCreatedBy() != null) {
            dto.createdById   = t.getCreatedBy().getId();
            dto.createdByName = t.getCreatedBy().getName();
        }
        return dto;
    }

    // ── Getters & Setters ──────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPriority() { return priority; }
    public void setPriority(String priority) { this.priority = priority; }

    public Long getProjectId() { return projectId; }
    public void setProjectId(Long projectId) { this.projectId = projectId; }

    public String getProjectTitle() { return projectTitle; }
    public void setProjectTitle(String projectTitle) { this.projectTitle = projectTitle; }

    public Long getAssignedToId() { return assignedToId; }
    public void setAssignedToId(Long assignedToId) { this.assignedToId = assignedToId; }

    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }

    public Long getCreatedById() { return createdById; }
    public void setCreatedById(Long createdById) { this.createdById = createdById; }

    public String getCreatedByName() { return createdByName; }
    public void setCreatedByName(String createdByName) { this.createdByName = createdByName; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
