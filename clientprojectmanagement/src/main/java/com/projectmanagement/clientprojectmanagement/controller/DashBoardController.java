package com.projectmanagement.clientprojectmanagement.controller;

import com.projectmanagement.clientprojectmanagement.dto.DashBoardStatsDTO;
import com.projectmanagement.clientprojectmanagement.dto.ProjectDashBoardDTO;
import com.projectmanagement.clientprojectmanagement.model.Message;
import com.projectmanagement.clientprojectmanagement.model.Project;
import com.projectmanagement.clientprojectmanagement.repository.MessageRepository;
import com.projectmanagement.clientprojectmanagement.repository.ProjectRepository;
import com.projectmanagement.clientprojectmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clients")
@CrossOrigin(origins = "*")
public class DashBoardController {

    @Autowired private ProjectRepository projectRepository;
    @Autowired private MessageRepository  messageRepository;
    @Autowired private UserRepository     userRepository;

    // ── CLIENT PROJECTS ───────────────────────────────────────
    // GET /clients/{clientId}/projects
    @GetMapping("/{clientId}/projects")
    public List<ProjectDashBoardDTO> getClientProjects(@PathVariable Long clientId) {
        return projectRepository.findByClientId(clientId).stream()
                .map(p -> {
                    Message last = messageRepository
                            .findTopByProject_IdOrderByTimestampDesc(p.getId());
                    String lastMsg = last != null ? last.getContent() : "No messages yet";

                    return new ProjectDashBoardDTO(
                            p.getId(),
                            p.getTitle(),
                            p.getStatus(),
                            p.getDeadline(),
                            lastMsg,
                            p.getProgress()   // ← passing real progress
                    );
                })
                .toList();
    }

    // ── GLOBAL STATS ──────────────────────────────────────────
    // GET /clients/stats
    @GetMapping("/stats")
    public DashBoardStatsDTO getStats() {
        return new DashBoardStatsDTO(
                projectRepository.count(),
                projectRepository.countByStatus("active"),
                projectRepository.countByStatus("completed"),
                messageRepository.count()
        );
    }

    // ── CLIENT STATS ──────────────────────────────────────────
    // GET /clients/{clientId}/stats
    @GetMapping("/{clientId}/stats")
    public DashBoardStatsDTO getClientStats(@PathVariable Long clientId) {
        List<Project> projects = projectRepository.findByClientId(clientId);
        long active    = projects.stream().filter(p -> "active".equalsIgnoreCase(p.getStatus())).count();
        long completed = projects.stream().filter(p -> "completed".equalsIgnoreCase(p.getStatus())).count();
        long messages  = projects.stream()
                .mapToLong(p -> messageRepository.findByProjectIdOrderByTimestampAsc(p.getId()).size())
                .sum();
        return new DashBoardStatsDTO(projects.size(), active, completed, messages);
    }
}