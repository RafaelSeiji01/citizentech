package com.fiap.citizentech.application.bo;

import com.fiap.citizentech.domain.exception.RecursoNaoEncontradoException;
import com.fiap.citizentech.domain.exception.RegraNegocioException;
import com.fiap.citizentech.domain.model.Ocorrencia;
import com.fiap.citizentech.domain.model.Usuario;
import com.fiap.citizentech.infrastructure.dao.EstacaoDAO;
import com.fiap.citizentech.infrastructure.dao.OcorrenciaDAO;
import com.fiap.citizentech.infrastructure.dao.UsuarioDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OcorrenciaBO {

    // Mesmos valores usados no banco (TP_OCORRENCIA)
    public static final List<String> TIPOS_VALIDOS = List.of(
            "LOTACAO", "ATRASO", "PARALISACAO", "ESCADA_ROLANTE");

    // Pontos que o autor ganha quando a ocorrência dele é confirmada
    public static final int PONTOS_OCORRENCIA_CONFIRMADA = 10;

    private final OcorrenciaDAO ocorrenciaDao;
    private final EstacaoDAO estacaoDao;
    private final UsuarioDAO usuarioDao;

    public List<Ocorrencia> listarPorEstacao(Long idEstacao) throws SQLException {
        if (estacaoDao.buscarPorId(idEstacao) == null) {
            throw new RecursoNaoEncontradoException("Estação " + idEstacao + " não encontrada");
        }
        return ocorrenciaDao.listarPorEstacao(idEstacao);
    }

    public void registrarOcorrencia(Ocorrencia o) throws SQLException {
        String tipo = o.getTipo().toUpperCase();
        if (!TIPOS_VALIDOS.contains(tipo)) {
            throw new RegraNegocioException("Tipo inválido. Use: " + TIPOS_VALIDOS);
        }
        if (usuarioDao.buscarPorId(o.getIdUsuario()) == null) {
            throw new RecursoNaoEncontradoException("Usuário " + o.getIdUsuario() + " não encontrado");
        }
        if (estacaoDao.buscarPorId(o.getIdEstacao()) == null) {
            throw new RecursoNaoEncontradoException("Estação " + o.getIdEstacao() + " não encontrada");
        }

        // Valores iniciais definidos pelo sistema, não pelo usuário
        o.setTipo(tipo);
        o.setDataHora(LocalDateTime.now());
        o.setConfirmacoes(0);
        o.setStatus("ATIVA");
        ocorrenciaDao.inserir(o);
    }

    public Ocorrencia confirmarOcorrencia(Long id) throws SQLException {
        Ocorrencia ocorrencia = buscarPorId(id);
        if ("RESOLVIDA".equals(ocorrencia.getStatus())) {
            throw new RegraNegocioException("Não é possível confirmar uma ocorrência resolvida");
        }

        ocorrencia.incrementarConfirmacao();

        // Acabou de atingir o mínimo: o autor ganha reputação (só uma vez, na passagem exata)
        if (ocorrencia.getConfirmacoes() == Ocorrencia.CONFIRMACOES_NECESSARIAS) {
            Usuario autor = usuarioDao.buscarPorId(ocorrencia.getIdUsuario());
            if (autor != null) {
                autor.adicionarReputacao(PONTOS_OCORRENCIA_CONFIRMADA);
                usuarioDao.atualizarReputacao(autor.getId(), autor.getPontosReputacao());
            }
        }

        ocorrenciaDao.atualizar(ocorrencia);
        avaliarImpactoEstacao(ocorrencia.getIdEstacao());
        return ocorrencia;
    }

    /**
     * Recalcula o status da estação a partir das ocorrências CONFIRMADAS
     * (ATIVA e com pelo menos 3 confirmações).
     * Prioridade: PARALISADA > LENTIDAO > ATENCAO > NORMAL
     */
    public void avaliarImpactoEstacao(Long idEstacao) throws SQLException {
        List<Ocorrencia> confirmadas = ocorrenciaDao.listarPorEstacao(idEstacao).stream()
                .filter(Ocorrencia::isConfirmada)
                .toList();

        String novoStatus = "NORMAL";
        for (Ocorrencia o : confirmadas) {
            if ("PARALISACAO".equals(o.getTipo())) {
                novoStatus = "PARALISADA";
                break;   // pior caso possível, não precisa olhar o resto
            }
            if ("ATRASO".equals(o.getTipo()) || "LOTACAO".equals(o.getTipo())) {
                novoStatus = "LENTIDAO";
            } else if ("NORMAL".equals(novoStatus)) {
                novoStatus = "ATENCAO";   // ex: ESCADA_ROLANTE
            }
        }
        estacaoDao.atualizarStatus(idEstacao, novoStatus);
    }

    public Ocorrencia resolverOcorrencia(Long id) throws SQLException {
        Ocorrencia ocorrencia = buscarPorId(id);
        if ("RESOLVIDA".equals(ocorrencia.getStatus())) {
            throw new RegraNegocioException("Esta ocorrência já está resolvida");
        }
        ocorrencia.setStatus("RESOLVIDA");
        ocorrenciaDao.atualizar(ocorrencia);
        avaliarImpactoEstacao(ocorrencia.getIdEstacao());
        return ocorrencia;
    }

    public void deletarOcorrencia(Long id) throws SQLException {
        Ocorrencia ocorrencia = buscarPorId(id);
        ocorrenciaDao.deletar(id);
        avaliarImpactoEstacao(ocorrencia.getIdEstacao());
    }

    private Ocorrencia buscarPorId(Long id) throws SQLException {
        Ocorrencia ocorrencia = ocorrenciaDao.buscarPorId(id);
        if (ocorrencia == null) {
            throw new RecursoNaoEncontradoException("Ocorrência " + id + " não encontrada");
        }
        return ocorrencia;
    }
}