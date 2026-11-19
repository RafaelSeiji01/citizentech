package com.fiap.citizentech.infrastructure.dao;

import com.fiap.citizentech.domain.model.LinhaEstacao;
import com.fiap.citizentech.infrastructure.database.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO da tabela associativa TB_MTR_LINHA_ESTACAO (N:N entre linha e estação).
 * A chave primária é composta: (ID_LINHA, ID_ESTACAO).
 */
@Repository
@RequiredArgsConstructor
public class LinhaEstacaoDAO {

    private final ConnectionFactory connectionFactory;

    // Sem getGeneratedKeys: a chave é composta e já vem preenchida (idLinha + idEstacao)
    public void inserir(LinhaEstacao le) throws SQLException {
        String sql = "INSERT INTO TB_MTR_LINHA_ESTACAO (ID_LINHA, ID_ESTACAO, NR_ORDEM) VALUES (?, ?, ?)";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, le.getIdLinha());
            ps.setLong(2, le.getIdEstacao());
            ps.setInt(3, le.getOrdem());
            ps.executeUpdate();
        }
    }

    // Trajeto da linha: estações na ordem, com nome e status (JOIN)
    public List<LinhaEstacao> listarPorLinha(Long idLinha) throws SQLException {
        String sql = "SELECT LE.ID_LINHA, LE.ID_ESTACAO, LE.NR_ORDEM, E.NM_ESTACAO, E.ST_ESTACAO "
                + "FROM TB_MTR_LINHA_ESTACAO LE "
                + "JOIN TB_MTR_ESTACAO E ON E.ID_ESTACAO = LE.ID_ESTACAO "
                + "WHERE LE.ID_LINHA = ? ORDER BY LE.NR_ORDEM";
        List<LinhaEstacao> lista = new ArrayList<>();

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, idLinha);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarLinhaEstacao(rs));
                }
            }
        }
        return lista;
    }

    // Busca pela chave composta: a estação já está nesta linha?
    public LinhaEstacao buscar(Long idLinha, Long idEstacao) throws SQLException {
        String sql = "SELECT LE.ID_LINHA, LE.ID_ESTACAO, LE.NR_ORDEM, E.NM_ESTACAO, E.ST_ESTACAO "
                + "FROM TB_MTR_LINHA_ESTACAO LE "
                + "JOIN TB_MTR_ESTACAO E ON E.ID_ESTACAO = LE.ID_ESTACAO "
                + "WHERE LE.ID_LINHA = ? AND LE.ID_ESTACAO = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, idLinha);
            ps.setLong(2, idEstacao);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montarLinhaEstacao(rs) : null;
            }
        }
    }

    // A posição já está ocupada por outra estação nesta linha?
    public boolean existeOrdem(Long idLinha, Integer ordem) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TB_MTR_LINHA_ESTACAO WHERE ID_LINHA = ? AND NR_ORDEM = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, idLinha);
            ps.setInt(2, ordem);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        }
    }

    public int contarPorLinha(Long idLinha) throws SQLException {
        String sql = "SELECT COUNT(*) FROM TB_MTR_LINHA_ESTACAO WHERE ID_LINHA = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, idLinha);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        }
    }

    public void deletar(Long idLinha, Long idEstacao) throws SQLException {
        String sql = "DELETE FROM TB_MTR_LINHA_ESTACAO WHERE ID_LINHA = ? AND ID_ESTACAO = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, idLinha);
            ps.setLong(2, idEstacao);
            ps.executeUpdate();
        }
    }

    private LinhaEstacao montarLinhaEstacao(ResultSet rs) throws SQLException {
        return new LinhaEstacao(
                rs.getLong("ID_LINHA"),
                rs.getLong("ID_ESTACAO"),
                rs.getInt("NR_ORDEM"),
                rs.getString("NM_ESTACAO"),
                rs.getString("ST_ESTACAO")
        );
    }
}