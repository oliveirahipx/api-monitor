package com.seumonitor.api_tester.Service;

import com.seumonitor.api_tester.Model.User;
import com.seumonitor.api_tester.Repository.UserRepository;
import org.springframework.stereotype.Service;
import java.util.Optional;
import jakarta.annotation.PostConstruct; // Para rodar código logo após a inicialização

@Service
public class UserService {

    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Simulação de Autenticação: Valida email e senha.
     */
    public Optional<User> authenticate(String email, String password) {
        Optional<User> userOptional = userRepository.findByEmail(email);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            // Simulação de verificação de senha
            if (user.getPassword().equals(password)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }


    @PostConstruct
    public void setupInitialUser() {
        if (userRepository.count() == 0) {
            User initialUser = new User(
                    "Usuário Demo",
                    "demo@exemplo.com",
                    "123456", // Senha de exemplo
                    "Meu projeto",
                    "https://jsonplaceholder.typicode.com"
            );
            userRepository.save(initialUser);
            System.out.println("Usuário inicial criado: demo@exemplo.com / 123456");
        }
    }

    /**
     * Busca um usuário pelo email.
     */
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }


    /**
     * Atualiza nome, e-mail, projeto e URL base. A senha só muda se vier preenchida.
     */
    public Optional<User> updateUser(User dados) {
        if (dados.getId() == null) {
            return Optional.empty();
        }
        return userRepository.findById(dados.getId()).map(user -> {
            user.setName(dados.getName());
            user.setEmail(dados.getEmail());
            user.setProjectName(dados.getProjectName());
            user.setBaseUrl(dados.getBaseUrl());
            if (dados.getPassword() != null && !dados.getPassword().isBlank()) {
                user.setPassword(dados.getPassword());
            }
            return userRepository.save(user);
        });
    }
}
