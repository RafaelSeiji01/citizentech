package com.fiap.citizentech.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

/**
 * Problema reportado por um usuário em uma estação (lentidão, paralisação,
 * superlotação). Outros usuários confirmam a ocorrência; ao atingir o
 * mínimo de confirmações ela passa a afetar o status da estação.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Ocorrencia {

    private Long id;

    @NotNull(message = "O id do usuário é obrigatório")
    private Long idUsuario;

    @NotNull(message = "O id da estação é obrigatório")
    private Long idEstacao;

    @NotBlank(message = "O tipo da ocorrência é obrigatório")
    private String tipo;

    @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
    private String descricao;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dataHora;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer confirmacoes;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String status;

    // Comportamento do domínio: continua escrito à mão
    public void incrementarConfirmacao() {
        this.confirmacoes = (confirmacoes == null ? 0 : confirmacoes) + 1;
    }
}