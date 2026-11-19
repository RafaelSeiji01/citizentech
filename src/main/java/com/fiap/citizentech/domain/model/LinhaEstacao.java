
package com.fiap.citizentech.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Ligação N:N entre Linha e Estacao, com a posição da estação na linha.
 * Uma estação de integração (ex: Sé) aparece uma vez para cada linha.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LinhaEstacao {

    // Vem da URL: POST /linhas/{idLinha}/estacoes
    private Long idLinha;

    @NotNull(message = "O id da estação é obrigatório")
    private Long idEstacao;

    @NotNull(message = "A ordem é obrigatória")
    @Positive(message = "A ordem deve ser positiva")
    private Integer ordem;

    // Preenchidos na listagem (JOIN), para o mapa mostrar nome e status
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nomeEstacao;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String statusEstacao;
}