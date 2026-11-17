package com.seumonitor.api_tester.Controller;

import com.seumonitor.api_tester.DTO.LoginRequest;
import com.seumonitor.api_tester.Model.User;
import com.seumonitor.api_tester.Service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;

import java.util.Optional;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Endpoint para autenticação de usuário.
     * Recebe LoginRequest (email, password) e retorna o usuário (ou erro).
     */
    @PostMapping("/login")
    public ResponseEntity<User> login(@RequestBody LoginRequest loginRequest) {

        Optional<User> userOptional = userService.authenticate(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        );

        if (userOptional.isPresent()) {
            // Sucesso: Retorna o objeto User com status 200
            return ResponseEntity.ok(userOptional.get());
        } else {
            // Falha: Retorna status 401 Unauthorized
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    /** Dados do usuário para a tela de Configurações. */
    @GetMapping("/user/{email}")
    public ResponseEntity<User> getUser(@PathVariable String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Salva as alterações feitas na tela de Configurações. */
    @PutMapping("/user/update")
    public ResponseEntity<User> updateUser(@RequestBody User dados) {
        return userService.updateUser(dados)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }
}
