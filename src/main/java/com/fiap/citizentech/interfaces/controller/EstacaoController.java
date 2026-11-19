package com.fiap.citizentech.interfaces.controller;

import com.fiap.citizentech.application.bo.EstacaoBO;
import com.fiap.citizentech.domain.model.Estacao;
import com.fiap.citizentech.interfaces.dto.StatusRequest;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.sql.SQLException;
import java.util.List;

@RestController
@RequestMapping("/estacoes")
@RequiredArgsConstructor
public class EstacaoController {

    private final EstacaoBO estacaoBo;

    // GET /estacoes  -> todas, em ordem alfabética
// (estações de uma linha, na ordem do trajeto: GET /linhas/{id}/estacoes)
    @GetMapping
    public ResponseEntity<List<Estacao>> listarTodas() throws SQLException {
        return ResponseEntity.ok(estacaoBo.listarTodas());
    }
    // GET /estacoes/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Estacao> buscarPorId(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(estacaoBo.buscarPorId(id));
    }

    // POST /estacoes
    @PostMapping
    public ResponseEntity<Estacao> criar(@Valid @RequestBody Estacao e) throws SQLException {
        estacaoBo.criar(e);
        return ResponseEntity.status(HttpStatus.CREATED).body(e);
    }

    // PUT /estacoes/{id}/status   corpo: { "status": "LENTIDAO" }
    @PutMapping("/{id}/status")
    public ResponseEntity<Estacao> atualizarStatus(@PathVariable Long id,
                                                   @RequestBody StatusRequest body) throws SQLException {
        return ResponseEntity.ok(estacaoBo.atualizarStatus(id, body.status()));
    }

    // DELETE /estacoes/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws SQLException {
        estacaoBo.deletar(id);
        return ResponseEntity.noContent().build();
    }
}