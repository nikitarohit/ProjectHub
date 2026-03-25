package com.projectmanagement.clientprojectmanagement.controller;

import com.projectmanagement.clientprojectmanagement.model.User;
import com.projectmanagement.clientprojectmanagement.repository.UserRepository;
import com.projectmanagement.clientprojectmanagement.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/users")
@CrossOrigin(origins = "*")
public class UserController {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder passwordEncoder;

    // ── REGISTER ──────────────────────────────────────────────
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {

        // ── Validation ────────────────────────────────────────
        if (user.getName() == null || user.getName().trim().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Name is required"));

        if (user.getName().trim().length() < 2)
            return ResponseEntity.badRequest().body(Map.of("error", "Name must be at least 2 characters"));

        if (user.getEmail() == null || user.getEmail().trim().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));

        if (!user.getEmail().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
            return ResponseEntity.badRequest().body(Map.of("error", "Please enter a valid email address"));

        if (user.getPassword() == null || user.getPassword().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));

        if (user.getPassword().length() < 4)
            return ResponseEntity.badRequest().body(Map.of("error", "Password must be at least 4 characters"));

        // ── Check duplicate email ─────────────────────────────
        if (userRepository.existsByEmail(user.getEmail().trim().toLowerCase()))
            return ResponseEntity.badRequest().body(Map.of("error", "Email already registered. Please login."));

        // ── Role validation — allow admin, client, developer ──
        String role = user.getRole();
        if (role == null || role.trim().isEmpty()) {
            role = "client"; // default role
        }
        if (!role.equals("admin") && !role.equals("client") && !role.equals("developer")) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid role. Must be admin, client, or developer."));
        }

        // ── Hash password + save ──────────────────────────────
        user.setEmail(user.getEmail().trim().toLowerCase());
        user.setName(user.getName().trim());
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(role); // use validated role

        User saved = userRepository.save(user);

        // ── Generate JWT token ────────────────────────────────
        String token = jwtUtil.generateToken(saved.getId(), saved.getEmail(), saved.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", saved.getId());
        response.put("name", saved.getName());
        response.put("email", saved.getEmail());
        response.put("role", saved.getRole());
        return ResponseEntity.ok(response);
    }

    // ── LOGIN ─────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Map<String, String> body) {

        String email = body.get("email");
        String password = body.get("password");

        if (email == null || email.trim().isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Email is required"));

        if (password == null || password.isEmpty())
            return ResponseEntity.badRequest().body(Map.of("error", "Password is required"));

        User user = userRepository.findByEmail(email.trim().toLowerCase());
        if (user == null)
            return ResponseEntity.status(401).body(Map.of("error", "No account found with this email"));

        if (!passwordEncoder.matches(password, user.getPassword()))
            return ResponseEntity.status(401).body(Map.of("error", "Incorrect password"));

        String token = jwtUtil.generateToken(user.getId(), user.getEmail(), user.getRole());

        Map<String, Object> response = new HashMap<>();
        response.put("token", token);
        response.put("id", user.getId());
        response.put("name", user.getName());
        response.put("email", user.getEmail());
        response.put("role", user.getRole());
        return ResponseEntity.ok(response);
    }

    // ── GET ALL USERS ─────────────────────────────────────────
    @GetMapping
    public List<User> getAll() {
        List<User> users = userRepository.findAll();
        users.forEach(u -> u.setPassword(null));
        return users;
    }

    // ── GET USER BY ID ────────────────────────────────────────
    @GetMapping("/{id:[0-9]+}")
    public ResponseEntity<?> getById(@PathVariable Long id) {
        return userRepository.findById(id).map(u -> {
            u.setPassword(null);
            return ResponseEntity.ok(u);
        }).orElse(ResponseEntity.notFound().build());
    }

    // ── UPDATE USER ───────────────────────────────────────────
    @PutMapping("/{id:[0-9]+}")
    public ResponseEntity<?> update(@PathVariable Long id,
                                    @RequestBody Map<String, String> body) {
        return userRepository.findById(id).map(u -> {
            if (body.containsKey("name") && !body.get("name").trim().isEmpty())
                u.setName(body.get("name").trim());
            if (body.containsKey("email") && !body.get("email").trim().isEmpty())
                u.setEmail(body.get("email").trim().toLowerCase());
            if (body.containsKey("password") && !body.get("password").isEmpty())
                u.setPassword(passwordEncoder.encode(body.get("password")));
            User saved = userRepository.save(u);
            saved.setPassword(null);
            return ResponseEntity.ok(saved);
        }).orElse(ResponseEntity.notFound().build());
    }
}