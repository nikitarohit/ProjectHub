package com.projectmanagement.clientprojectmanagement.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "projects")
public class Project {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    private String description;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate deadline;

    private String status;

    // ── NEW FIELD ─────────────────────────────────────────────
    // Progress percentage 0–100
    // Default is 0 when project is created
    @Column(columnDefinition = "INT DEFAULT 0")
    private Integer progress = 0;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private User client;

    // ── GETTERS & SETTERS ─────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDate getDeadline() {
        return deadline; }
    public void setDeadline(LocalDate deadline) { this.deadline = deadline; }

    public String getStatus() {
        return status;
    }
    public void setStatus(String status) { this.status = status; }

    public Integer getProgress() { return progress != null ? progress : 0; }
    public void setProgress(Integer progress) { this.progress = progress; }

    public User getClient() { return client; }
    public void setClient(User client) { this.client = client; }
}