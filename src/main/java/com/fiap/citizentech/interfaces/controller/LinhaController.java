package com.fiap.citizentech.interfaces.controller;

import com.fiap.citizentech.application.bo.LinhaBO;
import com.fiap.citizentech.domain.model.Linha;
import com.fiap.citizentech.domain.model.LinhaEstacao;
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
@RequestMapping("/linhas")
@RequiredArgsConstructor
public class LinhaController {

    private final LinhaBO linhaBo;

    // GET /linhas
    @GetMapping
    public ResponseEntity<List<Linha>> listarTodas() throws SQLException {
        return ResponseEntity.ok(linhaBo.listarTodas());
    }

    // GET /linhas/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Linha> buscarPorId(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(linhaBo.buscarPorId(id));
    }

    // POST /linhas
    @PostMapping
    public ResponseEntity<Linha> criar(@Valid @RequestBody Linha l) throws SQLException {
        linhaBo.criar(l);
        return ResponseEntity.status(HttpStatus.CREATED).body(l);
    }

    // PUT /linhas/{id}
    @PutMapping("/{id}")
    public ResponseEntity<Linha> atualizar(@PathVariable Long id,
                                           @Valid @RequestBody Linha l) throws SQLException {
        return ResponseEntity.ok(linhaBo.atualizar(id, l));
    }

    // DELETE /linhas/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws SQLException {
        linhaBo.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // GET /linhas/{id}/estacoes  -> trajeto da linha, na ordem (para o mapa)
    @GetMapping("/{id}/estacoes")
    public ResponseEntity<List<LinhaEstacao>> listarEstacoes(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(linhaBo.listarEstacoes(id));
    }

    // POST /linhas/{id}/estacoes   corpo: { "idEstacao": 5, "ordem": 3 }
    @PostMapping("/{id}/estacoes")
    public ResponseEntity<LinhaEstacao> adicionarEstacao(@PathVariable Long id,
                                                         @Valid @RequestBody LinhaEstacao le) throws SQLException {
        return ResponseEntity.status(HttpStatus.CREATED).body(linhaBo.adicionarEstacao(id, le));
    }

    // DELETE /linhas/{idLinha}/estacoes/{idEstacao}
    @DeleteMapping("/{idLinha}/estacoes/{idEstacao}")
    public ResponseEntity<Void> removerEstacao(@PathVariable Long idLinha,
                                               @PathVariable Long idEstacao) throws SQLException {
        linhaBo.removerEstacao(idLinha, idEstacao);
        return ResponseEntity.noContent().build();
    }
}