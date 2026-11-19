package com.fiap.citizentech.interfaces.handler;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Formato padrão de todas as respostas de erro da API.
 */
public record ErroResponse(
        LocalDateTime dataHora,
        int status,
        String erro,
        String mensagem,
        List<String> detalhes
) {
}