package com.fiap.citizentech.application.bo;

import com.fiap.citizentech.domain.exception.RecursoNaoEncontradoException;
import com.fiap.citizentech.domain.exception.RegraNegocioException;
import com.fiap.citizentech.domain.model.Linha;
import com.fiap.citizentech.domain.model.LinhaEstacao;
import com.fiap.citizentech.infrastructure.dao.EstacaoDAO;
import com.fiap.citizentech.infrastructure.dao.LinhaDAO;
import com.fiap.citizentech.infrastructure.dao.LinhaEstacaoDAO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.SQLException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class LinhaBO {

    private final LinhaDAO linhaDao;
    private final LinhaEstacaoDAO linhaEstacaoDao;
    private final EstacaoDAO estacaoDao;

    // ---------------------------------------------------------------- Linha

    public List<Linha> listarTodas() throws SQLException {
        return linhaDao.listarTodas();
    }

    public Linha buscarPorId(Long id) throws SQLException {
        Linha linha = linhaDao.buscarPorId(id);
        if (linha == null) {
            throw new RecursoNaoEncontradoException("Linha " + id + " não encontrada");
        }
        return linha;
    }

    public void criar(Linha l) throws SQLException {
        validarNumero(l.getNumero());
        // Regra: o número da linha é único (NR_LINHA UNIQUE)
        if (linhaDao.buscarPorNumero(l.getNumero()) != null) {
            throw new RegraNegocioException("Já existe uma linha com o número " + l.getNumero());
        }
        linhaDao.inserir(l);
    }

    public Linha atualizar(Long id, Linha dados) throws SQLException {
        Linha existente = buscarPorId(id);
        validarNumero(dados.getNumero());

        // Se trocou o número, o novo número não pode ser de outra linha
        Linha donaDoNumero = linhaDao.buscarPorNumero(dados.getNumero());
        if (donaDoNumero != null && !donaDoNumero.getId().equals(id)) {
            throw new RegraNegocioException("Já existe uma linha com o número " + dados.getNumero());
        }

        existente.setNumero(dados.getNumero());
        existente.setNome(dados.getNome());
        existente.setCor(dados.getCor());
        linhaDao.atualizar(existente);
        return existente;
    }

    public void deletar(Long id) throws SQLException {
        buscarPorId(id);
        // Regra: não apagar uma linha que ainda tem estações no trajeto
        if (linhaEstacaoDao.contarPorLinha(id) > 0) {
            throw new RegraNegocioException("A linha possui estações. Remova as estações da linha antes de excluí-la");
        }
        linhaDao.deletar(id);
    }

    // ---------------------------------------------------------------- Estações da linha (N:N)

    public List<LinhaEstacao> listarEstacoes(Long idLinha) throws SQLException {
        buscarPorId(idLinha);
        return linhaEstacaoDao.listarPorLinha(idLinha);
    }

    public LinhaEstacao adicionarEstacao(Long idLinha, LinhaEstacao le) throws SQLException {
        buscarPorId(idLinha);
        if (estacaoDao.buscarPorId(le.getIdEstacao()) == null) {
            throw new RecursoNaoEncontradoException("Estação " + le.getIdEstacao() + " não encontrada");
        }
        if (le.getOrdem() == null || le.getOrdem() <= 0) {
            throw new RegraNegocioException("A ordem deve ser um número maior que zero");
        }
        // Regra: a mesma estação não pode aparecer duas vezes na mesma linha (PK composta)
        if (linhaEstacaoDao.buscar(idLinha, le.getIdEstacao()) != null) {
            throw new RegraNegocioException("A estação " + le.getIdEstacao() + " já pertence a esta linha");
        }
        // Regra: duas estações não podem ocupar a mesma posição na linha
        if (linhaEstacaoDao.existeOrdem(idLinha, le.getOrdem())) {
            throw new RegraNegocioException("A posição " + le.getOrdem() + " já está ocupada nesta linha");
        }

        le.setIdLinha(idLinha);
        linhaEstacaoDao.inserir(le);
        // Busca de novo para devolver com nome e status da estação (JOIN)
        return linhaEstacaoDao.buscar(idLinha, le.getIdEstacao());
    }

    public void removerEstacao(Long idLinha, Long idEstacao) throws SQLException {
        if (linhaEstacaoDao.buscar(idLinha, idEstacao) == null) {
            throw new RecursoNaoEncontradoException(
                    "A estação " + idEstacao + " não pertence à linha " + idLinha);
        }
        linhaEstacaoDao.deletar(idLinha, idEstacao);
    }

    // NR_LINHA é NUMBER(2): aceita de 1 a 99
    private void validarNumero(Integer numero) {
        if (numero == null || numero < 1 || numero > 99) {
            throw new RegraNegocioException("O número da linha deve estar entre 1 e 99");
        }
    }
}