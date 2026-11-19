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
        String sql = "INSERT INTO TB_MTR_ESTACAO (NM_ESTACAO, ST_ESTACAO) VALUES (?, ?)";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"ID_ESTACAO"})) {

            ps.setString(1, e.getNome());
            ps.setString(2, e.getStatusAtual());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    e.setId(rs.getLong(1));
                }
            }
        }
    }

    public List<Estacao> listarTodas() throws SQLException {
        String sql = "SELECT * FROM TB_MTR_ESTACAO ORDER BY NM_ESTACAO";
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

    // Usado pelo BO: o nome da estação é único (cada estação existe uma vez só)
    public Estacao buscarPorNome(String nome) throws SQLException {
        String sql = "SELECT * FROM TB_MTR_ESTACAO WHERE LOWER(NM_ESTACAO) = LOWER(?)";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, nome);
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

    /**
     * Apaga a estação e tudo que depende dela (ligações com linhas,
     * mensagens e ocorrências), numa única TRANSAÇÃO.
     * Assim funciona mesmo que as chaves estrangeiras não tenham ON DELETE CASCADE.
     */
    public void deletar(Long id) throws SQLException {
        String[] comandos = {
                "DELETE FROM TB_MTR_LINHA_ESTACAO WHERE ID_ESTACAO = ?",
                "DELETE FROM TB_MTR_MENSAGEM_CHAT WHERE ID_ESTACAO = ?",
                "DELETE FROM TB_MTR_OCORRENCIA WHERE ID_ESTACAO = ?",
                "DELETE FROM TB_MTR_ESTACAO WHERE ID_ESTACAO = ?"
        };

        try (Connection conexao = connectionFactory.obterConexao()) {
            conexao.setAutoCommit(false);   // inicia a transação
            try {
                for (String sql : comandos) {
                    try (PreparedStatement ps = conexao.prepareStatement(sql)) {
                        ps.setLong(1, id);
                        ps.executeUpdate();
                    }
                }
                conexao.commit();           // confirma tudo
            } catch (SQLException e) {
                conexao.rollback();         // desfaz tudo se algo falhar
                throw e;
            }
        }
    }

    private Estacao montarEstacao(ResultSet rs) throws SQLException {
        return new Estacao(
                rs.getLong("ID_ESTACAO"),
                rs.getString("NM_ESTACAO"),
                rs.getString("ST_ESTACAO")
        );
    }
}