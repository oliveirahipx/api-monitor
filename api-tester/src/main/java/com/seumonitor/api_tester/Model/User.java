package com.seumonitor.api_tester.Model; // Pacote ajustado para o padrão que usamos antes

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import lombok.Getter;
import lombok.Setter;
// Importamos Lombok, mas adicionamos os construtores explicitamente

@Getter
@Setter
@Entity
@Table(name = "api_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;
    // Aceita a senha no JSON de entrada, mas nunca a devolve nas respostas
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    private String projectName;
    private String baseUrl;

    public User() {}


    public User(String name, String email, String password, String projectName, String baseUrl) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.projectName = projectName;
        this.baseUrl = baseUrl;
    }

}