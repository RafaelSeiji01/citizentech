package com.fiap.citizentech.infrastructure.dao;

import com.fiap.citizentech.domain.model.Ocorrencia;
import com.fiap.citizentech.infrastructure.database.ConnectionFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class OcorrenciaDAO {

    private final ConnectionFactory connectionFactory;

    public void inserir(Ocorrencia o) throws SQLException {
        String sql = "INSERT INTO TB_MTR_OCORRENCIA (ID_USUARIO, ID_ESTACAO, TP_OCORRENCIA, "
                + "DS_OCORRENCIA, DT_OCORRENCIA, NR_CONFIRMACOES, ST_OCORRENCIA) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"ID_OCORRENCIA"})) {

            ps.setLong(1, o.getIdUsuario());
            ps.setLong(2, o.getIdEstacao());
            ps.setString(3, o.getTipo());
            ps.setString(4, o.getDescricao());
            ps.setTimestamp(5, Timestamp.valueOf(o.getDataHora()));
            ps.setInt(6, o.getConfirmacoes());
            ps.setString(7, o.getStatus());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    o.setId(rs.getLong(1));
                }
            }
        }
    }

    public List<Ocorrencia> listarPorEstacao(Long idEstacao) throws SQLException {
        String sql = "SELECT * FROM TB_MTR_OCORRENCIA WHERE ID_ESTACAO = ? ORDER BY DT_OCORRENCIA DESC";
        List<Ocorrencia> lista = new ArrayList<>();

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, idEstacao);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarOcorrencia(rs));
                }
            }
        }
        return lista;
    }

    public Ocorrencia buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM TB_MTR_OCORRENCIA WHERE ID_OCORRENCIA = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montarOcorrencia(rs) : null;
            }
        }
    }

    public void atualizar(Ocorrencia o) throws SQLException {
        String sql = "UPDATE TB_MTR_OCORRENCIA SET TP_OCORRENCIA = ?, DS_OCORRENCIA = ?, "
                + "NR_CONFIRMACOES = ?, ST_OCORRENCIA = ? WHERE ID_OCORRENCIA = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, o.getTipo());
            ps.setString(2, o.getDescricao());
            ps.setInt(3, o.getConfirmacoes());
            ps.setString(4, o.getStatus());
            ps.setLong(5, o.getId());
            ps.executeUpdate();
        }
    }

    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM TB_MTR_OCORRENCIA WHERE ID_OCORRENCIA = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // Converte a linha atual do ResultSet em um objeto Ocorrencia
    private Ocorrencia montarOcorrencia(ResultSet rs) throws SQLException {
        return new Ocorrencia(
                rs.getLong("ID_OCORRENCIA"),
                rs.getLong("ID_USUARIO"),
                rs.getLong("ID_ESTACAO"),
                rs.getString("TP_OCORRENCIA"),
                rs.getString("DS_OCORRENCIA"),
                rs.getTimestamp("DT_OCORRENCIA").toLocalDateTime(),
                rs.getInt("NR_CONFIRMACOES"),
                rs.getString("ST_OCORRENCIA")
        );
    }
}