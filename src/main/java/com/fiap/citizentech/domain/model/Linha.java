package com.fiap.citizentech.domain.model;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Linha do metrô (ex: 1 - Azul).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Linha {

    private Long id;

    @NotNull(message = "O número da linha é obrigatório")
    @Positive(message = "O número da linha deve ser positivo")
    @Max(value = 99, message = "O número da linha deve ter no máximo 2 dígitos")
    private Integer numero;

    @NotBlank(message = "O nome da linha é obrigatório")
    @Size(max = 30, message = "O nome deve ter no máximo 30 caracteres")
    private String nome;

    @NotBlank(message = "A cor da linha é obrigatória")
    @Size(max = 20, message = "A cor deve ter no máximo 20 caracteres")
    private String cor;
}