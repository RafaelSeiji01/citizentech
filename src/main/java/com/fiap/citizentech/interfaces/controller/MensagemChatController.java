package com.fiap.citizentech.interfaces.controller;

import com.fiap.citizentech.application.bo.MensagemChatBO;
import com.fiap.citizentech.domain.model.MensagemChat;
import com.fiap.citizentech.interfaces.dto.TextoRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/mensagens")
@RequiredArgsConstructor
public class MensagemChatController {

    private final MensagemChatBO mensagemBo;

    // GET /mensagens/estacao/{idEstacao}  -> histórico do mural da estação
    @GetMapping("/estacao/{idEstacao}")
    public ResponseEntity<List<MensagemChat>> listarPorEstacao(@PathVariable Long idEstacao)
            throws SQLException {
        return ResponseEntity.ok(mensagemBo.listarHistorico(idEstacao));
    }

    // POST /mensagens
    @PostMapping
    public ResponseEntity<MensagemChat> enviar(@Valid @RequestBody MensagemChat m) throws SQLException {
        mensagemBo.enviarMensagem(m);
        return ResponseEntity.status(HttpStatus.CREATED).body(m);
    }

    // PUT /mensagens/{id}   corpo: { "texto": "novo texto" }
    @PutMapping("/{id}")
    public ResponseEntity<MensagemChat> editar(@PathVariable Long id,
                                               @RequestBody TextoRequest body) throws SQLException {
        return ResponseEntity.ok(mensagemBo.editarMensagem(id, body.texto()));
    }

    // DELETE /mensagens/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws SQLException {
        mensagemBo.deletarMensagem(id);
        return ResponseEntity.noContent().build();
    }
}