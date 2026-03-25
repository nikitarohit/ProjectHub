package com.projectmanagement.clientprojectmanagement.controller;

import com.projectmanagement.clientprojectmanagement.dto.TaskCommentDTO;
import com.projectmanagement.clientprojectmanagement.dto.TaskDTO;
import com.projectmanagement.clientprojectmanagement.model.Task;
import com.projectmanagement.clientprojectmanagement.model.TaskComment;
import com.projectmanagement.clientprojectmanagement.repository.ProjectRepository;
import com.projectmanagement.clientprojectmanagement.repository.TaskCommentRepository;
import com.projectmanagement.clientprojectmanagement.repository.TaskRepository;
import com.projectmanagement.clientprojectmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@CrossOrigin(origins = "*")
public class TaskController {

    @Autowired private TaskRepository        taskRepo;
    @Autowired private TaskCommentRepository commentRepo;
    @Autowired private ProjectRepository     projectRepo;
    @Autowired private UserRepository        userRepo;

    // ── Normalize status to uppercase ──────────────────────────────────────
    private String normalizeStatus(String status, String defaultVal) {
        if (status == null || status.isBlank()) return defaultVal.toUpperCase();
        // Map old lowercase values to new uppercase
        switch (status.toLowerCase()) {
            case "todo":        return "TODO";
            case "inprogress":
            case "in_progress": return "IN_PROGRESS";
            case "in_review":
            case "inreview":    return "IN_REVIEW";
            case "done":        return "DONE";
            default:            return status.toUpperCase();
        }
    }

    private String normalizePriority(String priority, String defaultVal) {
        if (priority == null || priority.isBlank()) return defaultVal.toUpperCase();
        return priority.toUpperCase(); // LOW, MEDIUM, HIGH
    }

    // ── TASK ENDPOINTS ─────────────────────────────────────────────────────

    /** GET /tasks/project/{projectId} */
    @GetMapping("/tasks/project/{projectId}")
    public List<TaskDTO> getTasksByProject(@PathVariable Long projectId) {
        return taskRepo.findByProjectId(projectId)
                .stream().map(TaskDTO::from).collect(Collectors.toList());
    }

    /** GET /tasks/developer/{userId} */
    @GetMapping("/tasks/developer/{userId}")
    public List<TaskDTO> getTasksByDeveloper(@PathVariable Long userId) {
        return taskRepo.findByAssignedToId(userId)
                .stream().map(TaskDTO::from).collect(Collectors.toList());
    }

    /** GET /tasks/{id} */
    @GetMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO> getTask(@PathVariable Long id) {
        return taskRepo.findById(id)
                .map(t -> ResponseEntity.ok(TaskDTO.from(t)))
                .orElse(ResponseEntity.notFound().build());
    }

    /** POST /tasks — create task */
    @PostMapping("/tasks")
    public ResponseEntity<TaskDTO> createTask(@RequestBody Map<String, Object> body) {
        Task task = new Task();
        task.setTitle((String) body.get("title"));
        task.setDescription((String) body.get("description"));

        // ← Always uppercase
        task.setStatus(normalizeStatus(
                body.getOrDefault("status", "TODO").toString(), "TODO"));
        task.setPriority(normalizePriority(
                body.getOrDefault("priority", "MEDIUM").toString(), "MEDIUM"));

        if (body.get("projectId") != null) {
            Long projectId = Long.valueOf(body.get("projectId").toString());
            projectRepo.findById(projectId).ifPresent(task::setProject);
        }
        if (body.get("assignedToId") != null) {
            Long devId = Long.valueOf(body.get("assignedToId").toString());
            userRepo.findById(devId).ifPresent(task::setAssignedTo);
        }
        if (body.get("createdById") != null) {
            Long adminId = Long.valueOf(body.get("createdById").toString());
            userRepo.findById(adminId).ifPresent(task::setCreatedBy);
        }

        task.setCreatedAt(LocalDateTime.now());
        task.setUpdatedAt(LocalDateTime.now());

        return ResponseEntity.ok(TaskDTO.from(taskRepo.save(task)));
    }

    /** PUT /tasks/{id} — update task */
    @PutMapping("/tasks/{id}")
    public ResponseEntity<TaskDTO> updateTask(@PathVariable Long id,
                                              @RequestBody Map<String, Object> body) {
        return taskRepo.findById(id).map(task -> {
            if (body.containsKey("title"))       task.setTitle((String) body.get("title"));
            if (body.containsKey("description")) task.setDescription((String) body.get("description"));

            // ← Always uppercase
            if (body.containsKey("priority"))
                task.setPriority(normalizePriority((String) body.get("priority"), "MEDIUM"));
            if (body.containsKey("status"))
                task.setStatus(normalizeStatus((String) body.get("status"), "TODO"));

            if (body.containsKey("assignedToId")) {
                Object raw = body.get("assignedToId");
                if (raw == null) {
                    task.setAssignedTo(null);
                } else {
                    Long devId = Long.valueOf(raw.toString());
                    userRepo.findById(devId).ifPresent(task::setAssignedTo);
                }
            }

            task.setUpdatedAt(LocalDateTime.now());
            return ResponseEntity.ok(TaskDTO.from(taskRepo.save(task)));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** PATCH /tasks/{id}/status — move card between columns */
    @PatchMapping("/tasks/{id}/status")
    public ResponseEntity<TaskDTO> updateStatus(@PathVariable Long id,
                                                @RequestBody Map<String, String> body) {
        return taskRepo.findById(id).map(task -> {
            // ← Always uppercase
            task.setStatus(normalizeStatus(body.get("status"), "TODO"));
            task.setUpdatedAt(LocalDateTime.now());
            return ResponseEntity.ok(TaskDTO.from(taskRepo.save(task)));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /tasks/{id} */
    @DeleteMapping("/tasks/{id}")
    public ResponseEntity<Void> deleteTask(@PathVariable Long id) {
        if (!taskRepo.existsById(id)) return ResponseEntity.notFound().build();
        List<TaskComment> comments = commentRepo.findByTaskIdOrderByCreatedAtAsc(id);
        commentRepo.deleteAll(comments);
        taskRepo.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    // ── COMMENT ENDPOINTS ──────────────────────────────────────────────────

    /** GET /tasks/{id}/comments */
    @GetMapping("/tasks/{id}/comments")
    public List<TaskCommentDTO> getComments(@PathVariable Long id) {
        return commentRepo.findByTaskIdOrderByCreatedAtAsc(id)
                .stream().map(TaskCommentDTO::from).collect(Collectors.toList());
    }

    /** POST /tasks/{id}/comments */
    @PostMapping("/tasks/{id}/comments")
    public ResponseEntity<TaskCommentDTO> addComment(@PathVariable Long id,
                                                     @RequestBody Map<String, Object> body) {
        return taskRepo.findById(id).map(task -> {
            TaskComment comment = new TaskComment();
            comment.setContent((String) body.get("content"));
            comment.setTask(task);
            comment.setCreatedAt(LocalDateTime.now());

            if (body.get("senderId") != null) {
                Long senderId = Long.valueOf(body.get("senderId").toString());
                userRepo.findById(senderId).ifPresent(comment::setSender);
            }

            return ResponseEntity.ok(TaskCommentDTO.from(commentRepo.save(comment)));
        }).orElse(ResponseEntity.notFound().build());
    }

    /** DELETE /tasks/comments/{commentId} */
    @DeleteMapping("/tasks/comments/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        if (!commentRepo.existsById(commentId)) return ResponseEntity.notFound().build();
        commentRepo.deleteById(commentId);
        return ResponseEntity.noContent().build();
    }
}