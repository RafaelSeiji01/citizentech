package com.fiap.citizentech.domain.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Estação do metrô. O statusAtual é atualizado automaticamente
 * conforme as ocorrências confirmadas pela comunidade.
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

    @NotBlank(message = "A linha é obrigatória")
    @Size(max = 30, message = "A linha deve ter no máximo 30 caracteres")
    private String linha;

    private String statusAtual;
}