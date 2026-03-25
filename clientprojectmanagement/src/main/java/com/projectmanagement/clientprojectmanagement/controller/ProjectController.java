package com.projectmanagement.clientprojectmanagement.controller;

import com.projectmanagement.clientprojectmanagement.model.Project;
import com.projectmanagement.clientprojectmanagement.repository.ProjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/projects")
@CrossOrigin(origins = "*")
public class ProjectController {

    @Autowired
    private ProjectRepository projectRepository;

    // ── CREATE ────────────────────────────────────────────────
    @PostMapping
    public ResponseEntity<?> createProject(@RequestBody Project project) {
        if (project.getTitle() == null || project.getTitle().isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Title required"));
        if (project.getClient() == null || project.getClient().getId() == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Client required"));
        if (project.getProgress() == null) project.setProgress(0);
        return ResponseEntity.ok(projectRepository.save(project));
    }

    // ── GET ALL ───────────────────────────────────────────────
    @GetMapping
    public List<Project> getAll() { return projectRepository.findAll(); }

    // ── GET BY ID ─────────────────────────────────────────────
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return projectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    // ── GET BY CLIENT ─────────────────────────────────────────
    @GetMapping("/client/{clientId}")
    public List<Project> getByClient(@PathVariable Long clientId) {
        return projectRepository.findByClientId(clientId);
    }

    // ── UPDATE STATUS ─────────────────────────────────────────
    // PATCH /projects/{id}/status
    // Body: { "status": "completed" }
    @PatchMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id,
                                          @RequestBody Map<String, String> body) {
        String newStatus = body.get("status");
        if (newStatus == null || newStatus.isBlank())
            return ResponseEntity.badRequest().body(Map.of("error", "Status required"));
        if (!List.of("active", "pending", "completed").contains(newStatus.toLowerCase()))
            return ResponseEntity.badRequest().body(Map.of("error", "Status must be: active, pending, or completed"));

        return projectRepository.findById(id).map(p -> {
            p.setStatus(newStatus.toLowerCase());
            // Auto set 100% when completed
            if (newStatus.equalsIgnoreCase("completed")) p.setProgress(100);
            return ResponseEntity.ok(projectRepository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── UPDATE PROGRESS ───────────────────────────────────────
    // PATCH /projects/{id}/progress
    // Body: { "progress": 75 }
    @PatchMapping("/{id}/progress")
    public ResponseEntity<?> updateProgress(@PathVariable Long id,
                                            @RequestBody Map<String, Integer> body) {
        Integer progress = body.get("progress");
        if (progress == null)
            return ResponseEntity.badRequest().body(Map.of("error", "Progress required"));
        if (progress < 0 || progress > 100)
            return ResponseEntity.badRequest().body(Map.of("error", "Progress must be 0-100"));

        return projectRepository.findById(id).map(p -> {
            p.setProgress(progress);
            // Auto complete at 100%
            if (progress == 100) p.setStatus("completed");
            // Auto activate if was pending
            if (progress > 0 && "pending".equals(p.getStatus())) p.setStatus("active");
            return ResponseEntity.ok(projectRepository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── FULL UPDATE ───────────────────────────────────────────
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable Long id, @RequestBody Project updated) {
        return projectRepository.findById(id).map(p -> {
            if (updated.getTitle() != null)       p.setTitle(updated.getTitle());
            if (updated.getStatus() != null)      p.setStatus(updated.getStatus());
            if (updated.getDeadline() != null)    p.setDeadline(updated.getDeadline());
            if (updated.getDescription() != null) p.setDescription(updated.getDescription());
            if (updated.getProgress() != null)    p.setProgress(updated.getProgress());
            return ResponseEntity.ok(projectRepository.save(p));
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── DELETE ────────────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        if (!projectRepository.existsById(id)) return ResponseEntity.notFound().build();
        projectRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted"));
    }
}