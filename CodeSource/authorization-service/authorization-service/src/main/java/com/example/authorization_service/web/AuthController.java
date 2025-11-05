package com.example.authorization_service.web;

import com.example.authorization_service.web.dto.AuthRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Value("${security.productcomposite.admin.username:admin}")
    private String adminUsername;
    @Value("${security.productcomposite.admin.password:admin}")
    private String adminPassword;
    @Value("${security.productcomposite.user.username:user}")
    private String userUsername;
    @Value("${security.productcomposite.user.password:user}")
    private String userPassword;

    @PostMapping("/validate")
    public ResponseEntity<Void> validate(@Valid @RequestBody AuthRequest request) {
        String username = java.util.Objects.requireNonNull(request.getUsername());
        String password = java.util.Objects.requireNonNull(request.getPassword());
        String role = java.util.Objects.requireNonNull(request.getRole());

        boolean ok = false;
        if ("admin".equalsIgnoreCase(role)) {
            ok = adminUsername.equals(username) && adminPassword.equals(password);
        } else if ("user".equalsIgnoreCase(role)) {
            ok = userUsername.equals(username) && userPassword.equals(password);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        return ok ? ResponseEntity.ok().build() : ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
}

