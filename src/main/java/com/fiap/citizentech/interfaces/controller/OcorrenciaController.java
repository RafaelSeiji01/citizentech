package com.fiap.citizentech.interfaces.controller;

import com.fiap.citizentech.application.bo.OcorrenciaBO;
import com.fiap.citizentech.domain.model.Ocorrencia;
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
@RequestMapping("/ocorrencias")
@RequiredArgsConstructor
public class OcorrenciaController {

    private final OcorrenciaBO ocorrenciaBo;

    // GET /ocorrencias/estacao/{idEstacao}
    @GetMapping("/estacao/{idEstacao}")
    public ResponseEntity<List<Ocorrencia>> listarPorEstacao(@PathVariable Long idEstacao)
            throws SQLException {
        return ResponseEntity.ok(ocorrenciaBo.listarPorEstacao(idEstacao));
    }

    // POST /ocorrencias
    @PostMapping
    public ResponseEntity<Ocorrencia> criar(@Valid @RequestBody Ocorrencia o) throws SQLException {
        ocorrenciaBo.registrarOcorrencia(o);
        return ResponseEntity.status(HttpStatus.CREATED).body(o);
    }

    // PUT /ocorrencias/{id}/confirmar  -> "eu também estou vendo isso"
    @PutMapping("/{id}/confirmar")
    public ResponseEntity<Ocorrencia> confirmar(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(ocorrenciaBo.confirmarOcorrencia(id));
    }

    // PUT /ocorrencias/{id}/encerrar  -> problema resolvido
    @PutMapping("/{id}/encerrar")
    public ResponseEntity<Ocorrencia> encerrar(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(ocorrenciaBo.encerrarOcorrencia(id));
    }

    // DELETE /ocorrencias/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws SQLException {
        ocorrenciaBo.deletarOcorrencia(id);
        return ResponseEntity.noContent().build();
    }
}