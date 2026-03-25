package com.projectmanagement.clientprojectmanagement.dto;

public class DashBoardStatsDTO {

    private long totalProjects;
    private long activeprojects;
    private long completeprojects;
    private long totalmessages;

    public DashBoardStatsDTO(long totalProjects, long activeprojects, long completeprojects, long totalmessages) {
        this.totalProjects = totalProjects;
        this.activeprojects = activeprojects;
        this.completeprojects = completeprojects;
        this.totalmessages = totalmessages;
    }

    public long getTotalProjects() {
        return totalProjects;
    }

    public void setTotalProjects(long totalProjects) {
        this.totalProjects = totalProjects;
    }

    public long getActiveprojects() {
        return activeprojects;
    }

    public void setActiveprojects(long activeprojects) {
        this.activeprojects = activeprojects;
    }

    public long getCompleteprojects() {
        return completeprojects;
    }

    public void setCompleteprojects(long completeprojects) {
        this.completeprojects = completeprojects;
    }

    public long getTotalmessages() {
        return totalmessages;
    }

    public void setTotalmessages(long totalmessages) {
        this.totalmessages = totalmessages;
    }
}
