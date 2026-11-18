package com.fiap.citizentech.interfaces.controller;

import com.fiap.citizentech.application.bo.UsuarioBO;
import com.fiap.citizentech.domain.model.Usuario;
import com.fiap.citizentech.interfaces.dto.LoginRequest;
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
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioBO usuarioBo;

    // POST /usuarios  -> cadastro
    @PostMapping
    public ResponseEntity<Usuario> cadastrar(@Valid @RequestBody Usuario u) throws SQLException {
        usuarioBo.cadastrarUsuario(u);
        return ResponseEntity.status(HttpStatus.CREATED).body(u);
    }

    // POST /usuarios/login
    @PostMapping("/login")
    public ResponseEntity<Usuario> login(@RequestBody LoginRequest login) throws SQLException {
        Usuario usuario = usuarioBo.autenticar(login.email(), login.senha());
        return ResponseEntity.ok(usuario);
    }

    // GET /usuarios  -> ranking por reputação
    @GetMapping
    public ResponseEntity<List<Usuario>> listarTodos() throws SQLException {
        return ResponseEntity.ok(usuarioBo.listarTodos());
    }

    // GET /usuarios/{id}
    @GetMapping("/{id}")
    public ResponseEntity<Usuario> buscarPorId(@PathVariable Long id) throws SQLException {
        return ResponseEntity.ok(usuarioBo.buscarPorId(id));
    }

    // PUT /usuarios/{id}  -> editar perfil
    @PutMapping("/{id}")
    public ResponseEntity<Usuario> atualizar(@PathVariable Long id,
                                             @Valid @RequestBody Usuario u) throws SQLException {
        return ResponseEntity.ok(usuarioBo.atualizarUsuario(id, u));
    }

    // PUT /usuarios/{id}/reputacao?pontos=5
    @PutMapping("/{id}/reputacao")
    public ResponseEntity<Usuario> atualizarReputacao(@PathVariable Long id,
                                                      @RequestParam int pontos) throws SQLException {
        return ResponseEntity.ok(usuarioBo.atualizarReputacao(id, pontos));
    }

    // DELETE /usuarios/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) throws SQLException {
        usuarioBo.deletarUsuario(id);
        return ResponseEntity.noContent().build();
    }
}