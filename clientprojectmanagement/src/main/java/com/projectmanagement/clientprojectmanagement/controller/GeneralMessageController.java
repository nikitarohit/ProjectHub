package com.projectmanagement.clientprojectmanagement.controller;

import com.projectmanagement.clientprojectmanagement.dto.GeneralMessageDTO;
import com.projectmanagement.clientprojectmanagement.model.GeneralMessage;
import com.projectmanagement.clientprojectmanagement.model.User;
import com.projectmanagement.clientprojectmanagement.repository.GeneralMessageRepository;
import com.projectmanagement.clientprojectmanagement.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/general-messages")
@CrossOrigin(origins = "*")
public class GeneralMessageController {

    @Autowired
    private GeneralMessageRepository generalMessageRepository;

    @Autowired
    private UserRepository userRepository;

    // ── GET messages for a client ─────────────────────────────
    // GET /general-messages/client/{clientId}
    // Returns GeneralMessageDTO — NO password, NO extra data
    @GetMapping("/client/{clientId}")
    public List<GeneralMessageDTO> getMessages(@PathVariable Long clientId) {

        return generalMessageRepository
                .findByClient_IdOrderByTimestampAsc(clientId)
                .stream()
                .map(m -> new GeneralMessageDTO(
                        m.getId(),
                        m.getContent(),
                        m.getTimestamp(),
                        m.getSender() != null ? m.getSender().getId()   : null,
                        m.getSender() != null ? m.getSender().getName() : "Unknown",
                        m.getClient() != null ? m.getClient().getId()   : null
                ))
                .toList();
    }

    // ── GET all general messages (Admin use) ──────────────────
    // GET /general-messages
    // Admin dekhe kaun kaun se clients ne message kiya hai
    @GetMapping
    public List<GeneralMessageDTO> getAll() {
        return generalMessageRepository.findAll()
                .stream()
                .map(m -> new GeneralMessageDTO(
                        m.getId(),
                        m.getContent(),
                        m.getTimestamp(),
                        m.getSender() != null ? m.getSender().getId()   : null,
                        m.getSender() != null ? m.getSender().getName() : "Unknown",
                        m.getClient() != null ? m.getClient().getId()   : null
                ))
                .toList();
    }

    // ── SEND message ──────────────────────────────────────────
    // POST /general-messages
    // Body: { "content": "Hello", "senderId": 2, "clientId": 2 }
    @PostMapping
    public ResponseEntity<?> send(@RequestBody Map<String, Object> body) {

        // Validate inputs
        String content = (String) body.get("content");
        if (content == null || content.isBlank())
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Message content cannot be empty"));

        Object senderIdObj = body.get("senderId");
        Object clientIdObj = body.get("clientId");

        if (senderIdObj == null || clientIdObj == null)
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "senderId and clientId are required"));

        Long senderId = Long.valueOf(senderIdObj.toString());
        Long clientId = Long.valueOf(clientIdObj.toString());

        // Check sender exists
        Optional<User> senderOpt = userRepository.findById(senderId);
        if (senderOpt.isEmpty())
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Sender not found"));

        // Check client exists
        Optional<User> clientOpt = userRepository.findById(clientId);
        if (clientOpt.isEmpty())
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Client not found"));

        // Build and save entity
        GeneralMessage msg = new GeneralMessage();
        msg.setContent(content);
        msg.setTimestamp(LocalDateTime.now());
        msg.setSender(senderOpt.get());
        msg.setClient(clientOpt.get());

        GeneralMessage saved = generalMessageRepository.save(msg);

        // Return DTO — not entity (to hide passwords)
        return ResponseEntity.ok(new GeneralMessageDTO(
                saved.getId(),
                saved.getContent(),
                saved.getTimestamp(),
                saved.getSender().getId(),
                saved.getSender().getName(),
                saved.getClient().getId()
        ));
    }
}