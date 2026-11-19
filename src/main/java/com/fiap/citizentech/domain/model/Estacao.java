package com.fiap.citizentech.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Estação do metrô. Cada estação existe uma vez só; as linhas
 * em que ela aparece ficam na tabela associativa (LinhaEstacao).
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Estacao {

    private Long id;

    @NotBlank(message = "O nome da estação é obrigatório")
    @Size(max = 80, message = "O nome deve ter no máximo 80 caracteres")
    private String nome;

    private String statusAtual;
}