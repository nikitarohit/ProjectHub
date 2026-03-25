package com.projectmanagement.clientprojectmanagement.controller;

import com.projectmanagement.clientprojectmanagement.dto.MessageDTO;
import com.projectmanagement.clientprojectmanagement.model.Message;
import com.projectmanagement.clientprojectmanagement.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/messages")
@CrossOrigin(origins = "*")
public class MessageController {

    @Autowired
    private MessageRepository messageRepository;

    // ── GET ALL MESSAGES ──────────────────────────────────────
    @GetMapping
    public List<Message> getMessages() {
        return messageRepository.findAll();
    }

    // ── GET MESSAGES BY PROJECT ───────────────────────────────
    // GET /messages/project/{projectId}
    // Returns MessageDTO: { content, senderName, timestamp }
    // Used by project.html chat
    @GetMapping("/project/{projectId}")
    public List<MessageDTO> getMessagesByProject(@PathVariable Long projectId) {
        List<Message> messages = messageRepository
                .findByProjectIdOrderByTimestampAsc(projectId);

        return messages.stream()
                .map(m -> new MessageDTO(
                        m.getContent(),
                        m.getSender().getName(),
                        m.getTimestamp()
                ))
                .toList();
    }

    // ── SEND MESSAGE ──────────────────────────────────────────
    // POST /messages
    // Body: { "content": "Hello",
    //         "project": { "id": 1 },
    //         "sender":  { "id": 2 } }
    @PostMapping
    public ResponseEntity<?> sendMessage(@RequestBody Message message) {

        if (message.getContent() == null || message.getContent().isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Message content cannot be empty"));
        }

        if (message.getProject() == null || message.getProject().getId() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Project ID is required"));
        }

        if (message.getSender() == null || message.getSender().getId() == null) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", "Sender ID is required"));
        }

        message.setTimestamp(LocalDateTime.now());
        Message saved = messageRepository.save(message);
        return ResponseEntity.ok(saved);
    }

    // ── DELETE MESSAGE ────────────────────────────────────────
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteMessage(@PathVariable Long id) {
        if (!messageRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }
        messageRepository.deleteById(id);
        return ResponseEntity.ok(Map.of("message", "Deleted successfully"));
    }
}