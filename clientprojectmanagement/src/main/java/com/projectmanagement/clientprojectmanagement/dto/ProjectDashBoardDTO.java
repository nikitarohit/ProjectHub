package com.projectmanagement.clientprojectmanagement.dto;

import java.time.LocalDate;

public class ProjectDashBoardDTO {

    private Long id;
    private String title;
    private String status;
    private LocalDate deadline;
    private String lastMessage;
    private Integer progress;   // ← NEW

    public ProjectDashBoardDTO(Long id, String title, String status,
                               LocalDate deadline, String lastMessage, Integer progress) {
        this.id          = id;
        this.title       = title;
        this.status      = status;
        this.deadline    = deadline;
        this.lastMessage = lastMessage;
        this.progress    = progress != null ? progress : 0;
    }

    public Long    getId()          { return id; }
    public String  getTitle()       { return title; }
    public String  getStatus()      { return status; }
    public LocalDate getDeadline()  { return deadline; }
    public String  getLastMessage() { return lastMessage; }
    public Integer getProgress()    { return progress; }

    public void setId(Long id)                { this.id = id; }
    public void setTitle(String title)        { this.title = title; }
    public void setStatus(String status)      { this.status = status; }
    public void setDeadline(LocalDate d)      { this.deadline = d; }
    public void setLastMessage(String m)      { this.lastMessage = m; }
    public void setProgress(Integer progress) { this.progress = progress; }
}