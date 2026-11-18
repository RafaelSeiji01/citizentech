package com.fiap.citizentech.application.bo;

import com.fiap.citizentech.domain.exception.RecursoNaoEncontradoException;
import com.fiap.citizentech.domain.exception.RegraNegocioException;
import com.fiap.citizentech.domain.model.MensagemChat;
import com.fiap.citizentech.infrastructure.dao.EstacaoDAO;
import com.fiap.citizentech.infrastructure.dao.MensagemChatDAO;
import com.fiap.citizentech.infrastructure.dao.UsuarioDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MensagemChatBO {

    private final MensagemChatDAO mensagemDao;
    private final UsuarioDAO usuarioDao;
    private final EstacaoDAO estacaoDao;

    public void enviarMensagem(MensagemChat m) throws SQLException {
        // Regra: o usuário e a estação precisam existir
        if (usuarioDao.buscarPorId(m.getIdUsuario()) == null) {
            throw new RecursoNaoEncontradoException("Usuário " + m.getIdUsuario() + " não encontrado");
        }
        if (estacaoDao.buscarPorId(m.getIdEstacao()) == null) {
            throw new RecursoNaoEncontradoException("Estação " + m.getIdEstacao() + " não encontrada");
        }
        m.setTexto(m.getTexto().trim());
        m.setDataHora(LocalDateTime.now());   // a data é sempre definida pelo servidor
        mensagemDao.inserir(m);
    }

    public List<MensagemChat> listarHistorico(Long idEstacao) throws SQLException {
        if (estacaoDao.buscarPorId(idEstacao) == null) {
            throw new RecursoNaoEncontradoException("Estação " + idEstacao + " não encontrada");
        }
        return mensagemDao.listarPorEstacao(idEstacao);
    }

    public MensagemChat editarMensagem(Long id, String texto) throws SQLException {
        MensagemChat mensagem = buscarPorId(id);
        if (texto == null || texto.isBlank()) {
            throw new RegraNegocioException("O texto da mensagem não pode ficar vazio");
        }
        if (texto.length() > 500) {
            throw new RegraNegocioException("A mensagem deve ter no máximo 500 caracteres");
        }
        mensagemDao.atualizarTexto(id, texto.trim());
        mensagem.setTexto(texto.trim());
        return mensagem;
    }

    public void deletarMensagem(Long id) throws SQLException {
        buscarPorId(id);   // lança 404 se não existir
        mensagemDao.deletar(id);
    }

    // Busca a mensagem ou lança 404
    private MensagemChat buscarPorId(Long id) throws SQLException {
        MensagemChat mensagem = mensagemDao.buscarPorId(id);
        if (mensagem == null) {
            throw new RecursoNaoEncontradoException("Mensagem " + id + " não encontrada");
        }
        return mensagem;
    }
}