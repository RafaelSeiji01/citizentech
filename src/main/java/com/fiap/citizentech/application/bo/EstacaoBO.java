package com.fiap.citizentech.application.bo;

import com.fiap.citizentech.domain.exception.RecursoNaoEncontradoException;
import com.fiap.citizentech.domain.exception.RegraNegocioException;
import com.fiap.citizentech.domain.model.Estacao;
import com.fiap.citizentech.infrastructure.dao.EstacaoDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class EstacaoBO {

    // Únicos status aceitos para uma estação
    public static final List<String> STATUS_VALIDOS =
            List.of("NORMAL", "ATENCAO", "LENTIDAO", "PARALISADA");

    private final EstacaoDAO estacaoDao;

    public List<Estacao> listarTodas(String linha) throws SQLException {
        List<Estacao> estacoes = estacaoDao.listarTodas();
        if (linha == null || linha.isBlank()) {
            return estacoes;
        }
        // Filtro opcional: ?linha=1-Azul
        return estacoes.stream()
                .filter(e -> e.getLinha().equalsIgnoreCase(linha))
                .toList();
    }

    public Estacao buscarPorId(Long id) throws SQLException {
        Estacao estacao = estacaoDao.buscarPorId(id);
        if (estacao == null) {
            throw new RecursoNaoEncontradoException("Estação " + id + " não encontrada");
        }
        return estacao;
    }

    public void criar(Estacao e) throws SQLException {
        // Se não informar o status, a estação começa NORMAL
        if (e.getStatusAtual() == null || e.getStatusAtual().isBlank()) {
            e.setStatusAtual("NORMAL");
        }
        e.setStatusAtual(validarStatus(e.getStatusAtual()));
        estacaoDao.inserir(e);
    }

    public Estacao atualizarStatus(Long id, String status) throws SQLException {
        Estacao estacao = buscarPorId(id);
        String statusValidado = validarStatus(status);
        estacaoDao.atualizarStatus(id, statusValidado);
        estacao.setStatusAtual(statusValidado);
        return estacao;
    }

    public void deletar(Long id) throws SQLException {
        buscarPorId(id);   // lança 404 se não existir
        estacaoDao.deletar(id);
    }

    // Confere se o status está na lista e devolve em MAIÚSCULAS
    private String validarStatus(String status) {
        if (status == null || !STATUS_VALIDOS.contains(status.toUpperCase())) {
            throw new RegraNegocioException("Status inválido. Use: " + STATUS_VALIDOS);
        }
        return status.toUpperCase();
    }
}