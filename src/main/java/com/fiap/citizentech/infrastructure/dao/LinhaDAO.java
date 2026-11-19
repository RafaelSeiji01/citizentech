package com.fiap.citizentech.infrastructure.dao;

import com.fiap.citizentech.domain.model.Linha;
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
public class LinhaDAO {

    private final ConnectionFactory connectionFactory;

    public void inserir(Linha l) throws SQLException {
        String sql = "INSERT INTO TB_MTR_LINHA (NR_LINHA, NM_LINHA, DS_COR) VALUES (?, ?, ?)";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"ID_LINHA"})) {

            ps.setInt(1, l.getNumero());
            ps.setString(2, l.getNome());
            ps.setString(3, l.getCor());
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    l.setId(rs.getLong(1));
                }
            }
        }
    }

    public List<Linha> listarTodas() throws SQLException {
        String sql = "SELECT * FROM TB_MTR_LINHA ORDER BY NR_LINHA";
        List<Linha> lista = new ArrayList<>();

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(montarLinha(rs));
            }
        }
        return lista;
    }

    public Linha buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM TB_MTR_LINHA WHERE ID_LINHA = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montarLinha(rs) : null;
            }
        }
    }

    // Usado pelo BO para não deixar cadastrar duas linhas com o mesmo número
    public Linha buscarPorNumero(Integer numero) throws SQLException {
        String sql = "SELECT * FROM TB_MTR_LINHA WHERE NR_LINHA = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, numero);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montarLinha(rs) : null;
            }
        }
    }

    public void atualizar(Linha l) throws SQLException {
        String sql = "UPDATE TB_MTR_LINHA SET NR_LINHA = ?, NM_LINHA = ?, DS_COR = ? WHERE ID_LINHA = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, l.getNumero());
            ps.setString(2, l.getNome());
            ps.setString(3, l.getCor());
            ps.setLong(4, l.getId());
            ps.executeUpdate();
        }
    }

    /**
     * Apaga a linha. Antes, tira ela da linha favorita dos usuários
     * (senão a chave estrangeira do usuário impediria o DELETE).
     * As duas operações rodam numa TRANSAÇÃO: ou as duas dão certo, ou nenhuma.
     */
    public void deletar(Long id) throws SQLException {
        String sqlUsuarios = "UPDATE TB_MTR_USUARIO SET ID_LINHA_FAVORITA = NULL WHERE ID_LINHA_FAVORITA = ?";
        String sqlLinha = "DELETE FROM TB_MTR_LINHA WHERE ID_LINHA = ?";

        try (Connection conexao = connectionFactory.obterConexao()) {
            conexao.setAutoCommit(false);   // inicia a transação

            try (PreparedStatement psUsuarios = conexao.prepareStatement(sqlUsuarios);
                 PreparedStatement psLinha = conexao.prepareStatement(sqlLinha)) {

                psUsuarios.setLong(1, id);
                psUsuarios.executeUpdate();

                psLinha.setLong(1, id);
                psLinha.executeUpdate();

                conexao.commit();           // confirma as duas operações
            } catch (SQLException e) {
                conexao.rollback();         // desfaz tudo se algo falhar
                throw e;
            }
        }
    }

    private Linha montarLinha(ResultSet rs) throws SQLException {
        return new Linha(
                rs.getLong("ID_LINHA"),
                rs.getInt("NR_LINHA"),
                rs.getString("NM_LINHA"),
                rs.getString("DS_COR")
        );
    }
}