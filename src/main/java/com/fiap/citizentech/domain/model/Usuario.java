package com.fiap.citizentech.domain.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Usuário da comunidade. Pode reportar ocorrências e conversar
 * no mural (chat) de cada estação.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Size(max = 100, message = "O nome deve ter no máximo 100 caracteres")
    private String nome;

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    @Size(max = 120, message = "O e-mail deve ter no máximo 120 caracteres")
    private String email;

    // WRITE_ONLY: a senha é recebida no JSON, mas nunca é devolvida nas respostas
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotBlank(message = "A senha é obrigatória")
    @Size(min = 6, max = 100, message = "A senha deve ter entre 6 e 100 caracteres")
    private String senha;

    private Integer pontosReputacao;

    // Opcional: id da linha favorita (FK para TB_MTR_LINHA)
    private Long idLinhaFavorita;

    // Comportamento do domínio: a reputação nunca fica abaixo de zero
    public void adicionarReputacao(int pontos) {
        int atual = pontosReputacao == null ? 0 : pontosReputacao;
        this.pontosReputacao = Math.max(0, atual + pontos);
    }
}