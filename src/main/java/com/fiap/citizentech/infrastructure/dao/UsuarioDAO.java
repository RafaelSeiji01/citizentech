package com.fiap.citizentech.infrastructure.dao;

import com.fiap.citizentech.domain.model.Usuario;
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
public class UsuarioDAO {

    private final ConnectionFactory connectionFactory;

    public void inserir(Usuario u) throws SQLException {
        String sql = "INSERT INTO TB_MTR_USUARIO (NM_USUARIO, DS_EMAIL, DS_SENHA, "
                + "NR_PONTOS_REPUTACAO, DS_LINHA_FAVORITA) VALUES (?, ?, ?, ?, ?)";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"ID_USUARIO"})) {

            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getSenha());
            ps.setInt(4, u.getPontosReputacao());
            ps.setString(5, u.getLinhaFavorita());
            ps.executeUpdate();

            // Recupera o id gerado pelo banco e coloca no objeto
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    u.setId(rs.getLong(1));
                }
            }
        }
    }

    public Usuario buscarPorId(Long id) throws SQLException {
        String sql = "SELECT * FROM TB_MTR_USUARIO WHERE ID_USUARIO = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montarUsuario(rs) : null;
            }
        }
    }

    public Usuario buscarPorEmail(String email) throws SQLException {
        String sql = "SELECT * FROM TB_MTR_USUARIO WHERE LOWER(DS_EMAIL) = LOWER(?)";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montarUsuario(rs) : null;
            }
        }
    }

    public Usuario buscarPorEmailESenha(String email, String senha) throws SQLException {
        String sql = "SELECT * FROM TB_MTR_USUARIO WHERE LOWER(DS_EMAIL) = LOWER(?) AND DS_SENHA = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, email);
            ps.setString(2, senha);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montarUsuario(rs) : null;
            }
        }
    }

    public List<Usuario> listarTodos() throws SQLException {
        String sql = "SELECT * FROM TB_MTR_USUARIO ORDER BY NR_PONTOS_REPUTACAO DESC";
        List<Usuario> lista = new ArrayList<>();

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(montarUsuario(rs));
            }
        }
        return lista;
    }

    public void atualizar(Usuario u) throws SQLException {
        String sql = "UPDATE TB_MTR_USUARIO SET NM_USUARIO = ?, DS_EMAIL = ?, DS_SENHA = ?, "
                + "DS_LINHA_FAVORITA = ? WHERE ID_USUARIO = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, u.getNome());
            ps.setString(2, u.getEmail());
            ps.setString(3, u.getSenha());
            ps.setString(4, u.getLinhaFavorita());
            ps.setLong(5, u.getId());
            ps.executeUpdate();
        }
    }

    public void atualizarReputacao(Long id, int pontos) throws SQLException {
        String sql = "UPDATE TB_MTR_USUARIO SET NR_PONTOS_REPUTACAO = ? WHERE ID_USUARIO = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setInt(1, pontos);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM TB_MTR_USUARIO WHERE ID_USUARIO = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // Converte a linha atual do ResultSet em um objeto Usuario
    private Usuario montarUsuario(ResultSet rs) throws SQLException {
        return new Usuario(
                rs.getLong("ID_USUARIO"),
                rs.getString("NM_USUARIO"),
                rs.getString("DS_EMAIL"),
                rs.getString("DS_SENHA"),
                rs.getInt("NR_PONTOS_REPUTACAO"),
                rs.getString("DS_LINHA_FAVORITA")
        );
    }
}