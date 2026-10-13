package com.parking.controller;

import com.parking.model.*;
import com.parking.repo.UserRepository;
import com.parking.security.JwtUtil;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final UserRepository users; private final PasswordEncoder enc; private final JwtUtil jwt;
    public AuthController(UserRepository u, PasswordEncoder e, JwtUtil j) { users = u; enc = e; jwt = j; }

    record RegisterReq(@NotBlank String name, @Email @NotBlank String email, @Size(min = 6) String password) {}
    record LoginReq(@NotBlank String email, @NotBlank String password) {}

    @PostMapping("/register")
    public ResponseEntity<?> register(@Valid @RequestBody RegisterReq r) {
        if (users.findByEmail(r.email()).isPresent())
            return ResponseEntity.badRequest().body(Map.of("message", "Email already registered"));
        users.save(new User(r.name(), r.email(), enc.encode(r.password()), Role.USER));
        return ResponseEntity.ok(Map.of("message", "Registered successfully"));
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginReq r) {
        var u = users.findByEmail(r.email()).filter(x -> enc.matches(r.password(), x.getPassword()));
        if (u.isEmpty()) return ResponseEntity.status(401).body(Map.of("message", "Invalid email or password"));
        var user = u.get();
        return ResponseEntity.ok(Map.of("token", jwt.generate(user.getEmail(), user.getRole().name()),
                "name", user.getName(), "role", user.getRole().name()));
    }
}
