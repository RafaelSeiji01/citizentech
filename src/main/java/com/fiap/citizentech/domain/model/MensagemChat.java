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
 * Mensagem enviada por um usuário no mural (chat) de uma estação.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MensagemChat {

    private Long id;

    @NotNull(message = "O id do usuário é obrigatório")
    private Long idUsuario;

    @NotNull(message = "O id da estação é obrigatório")
    private Long idEstacao;

    // Preenchido na listagem para o front-end mostrar quem escreveu
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String nomeUsuario;

    @NotBlank(message = "O texto da mensagem é obrigatório")
    @Size(max = 500, message = "A mensagem deve ter no máximo 500 caracteres")
    private String texto;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private LocalDateTime dataHora;
}