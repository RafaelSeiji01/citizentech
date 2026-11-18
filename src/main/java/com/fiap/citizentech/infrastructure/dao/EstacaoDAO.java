package com.fiap.citizentech.infrastructure.dao;

import com.fiap.citizentech.domain.model.Estacao;
import com.fiap.citizentech.infrastructure.database.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class EstacaoDAO {

    private final ConnectionFactory connectionFactory;

    public void inserir(Estacao e) throws SQLException {
        String sql = "INSERT INTO TB_MTR_ESTACAO (NM_ESTACAO, DS_LINHA, ST_ESTACAO) VALUES (?, ?, ?)";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"ID_ESTACAO"})) {

            ps.setString(1, e.getNome());
            ps.setString(2, e.getLinha());
            ps.setString(3, e.getStatusAtual());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setId(rs.getLong(1));
                }
            }
        }
    }

    public List<Estacao> listarTodas() throws SQLException {
        String sql = "SELECT * FROM TB_MTR_ESTACAO ORDER BY DS_LINHA, ID_ESTACAO";
        List<Estacao> lista = new ArrayList<>();

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(montarEstacao(rs));
            }
        }
        return lista;
    }

    public Estacao buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM TB_MTR_ESTACAO WHERE ID_ESTACAO = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montarEstacao(rs) : null;
            }
        }
    }

    public void atualizarStatus(Long id, String novoStatus) throws SQLException {
        String sql = "UPDATE TB_MTR_ESTACAO SET ST_ESTACAO = ? WHERE ID_ESTACAO = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, novoStatus);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM TB_MTR_ESTACAO WHERE ID_ESTACAO = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // Converte a linha atual do ResultSet em um objeto Estacao
    private Estacao montarEstacao(ResultSet rs) throws SQLException {
        return new Estacao(
                rs.getLong("ID_ESTACAO"),
                rs.getString("NM_ESTACAO"),
                rs.getString("DS_LINHA"),
                rs.getString("ST_ESTACAO")
        );
    }
}