package com.fiap.citizentech.application.bo;

import com.fiap.citizentech.domain.exception.RecursoNaoEncontradoException;
import com.fiap.citizentech.domain.exception.RegraNegocioException;
import com.fiap.citizentech.domain.model.Usuario;
import com.fiap.citizentech.infrastructure.dao.LinhaDAO;
import com.fiap.citizentech.infrastructure.dao.UsuarioDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioBO {

    private final UsuarioDAO usuarioDao;
    private final LinhaDAO linhaDao;

    public void cadastrarUsuario(Usuario u) throws SQLException {
        // Regra: não pode existir dois usuários com o mesmo e-mail
        if (usuarioDao.buscarPorEmail(u.getEmail()) != null) {
            throw new RegraNegocioException("Já existe um usuário cadastrado com este e-mail");
        }
        validarLinhaFavorita(u.getIdLinhaFavorita());
        // Regra: todo usuário novo começa com 0 pontos de reputação
        u.setPontosReputacao(0);
        usuarioDao.inserir(u);
    }

    public Usuario autenticar(String email, String senha) throws SQLException {
        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            throw new RegraNegocioException("Informe o e-mail e a senha");
        }
        Usuario usuario = usuarioDao.buscarPorEmailESenha(email, senha);
        if (usuario == null) {
            throw new RegraNegocioException("E-mail ou senha inválidos");
        }
        return usuario;
    }

    public List<Usuario> listarTodos() throws SQLException {
        return usuarioDao.listarTodos();
    }

    public Usuario buscarPorId(Long id) throws SQLException {
        Usuario usuario = usuarioDao.buscarPorId(id);
        if (usuario == null) {
            throw new RecursoNaoEncontradoException("Usuário " + id + " não encontrado");
        }
        return usuario;
    }

    public Usuario atualizarUsuario(Long id, Usuario dados) throws SQLException {
        Usuario existente = buscarPorId(id);

        // Se trocou o e-mail, o novo e-mail não pode ser de outra pessoa
        Usuario donoDoEmail = usuarioDao.buscarPorEmail(dados.getEmail());
        if (donoDoEmail != null && !donoDoEmail.getId().equals(id)) {
            throw new RegraNegocioException("Este e-mail já está em uso por outro usuário");
        }

        validarLinhaFavorita(dados.getIdLinhaFavorita());

        existente.setNome(dados.getNome());
        existente.setEmail(dados.getEmail());
        existente.setSenha(dados.getSenha());
        existente.setIdLinhaFavorita(dados.getIdLinhaFavorita());
        usuarioDao.atualizar(existente);
        return existente;
    }

    public Usuario atualizarReputacao(Long id, int pontos) throws SQLException {
        Usuario usuario = buscarPorId(id);
        usuario.adicionarReputacao(pontos);   // regra fica no model: nunca abaixo de 0
        usuarioDao.atualizarReputacao(id, usuario.getPontosReputacao());
        return usuario;
    }

    public void deletarUsuario(Long id) throws SQLException {
        buscarPorId(id);   // lança 404 se não existir
        usuarioDao.deletar(id);
    }

    // A linha favorita é opcional, mas, se vier, precisa existir
    private void validarLinhaFavorita(Long idLinha) throws SQLException {
        if (idLinha != null && linhaDao.buscarPorId(idLinha) == null) {
            throw new RecursoNaoEncontradoException("Linha " + idLinha + " não encontrada");
        }
    }
}