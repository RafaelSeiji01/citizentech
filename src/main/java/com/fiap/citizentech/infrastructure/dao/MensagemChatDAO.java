package com.fiap.citizentech.infrastructure.dao;

import com.fiap.citizentech.domain.model.MensagemChat;
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
public class MensagemChatDAO {

    private final ConnectionFactory connectionFactory;

    public void inserir(MensagemChat m) throws SQLException {
        String sql = "INSERT INTO TB_MTR_MENSAGEM_CHAT (ID_USUARIO, ID_ESTACAO, DS_TEXTO, DT_ENVIO) "
                + "VALUES (?, ?, ?, ?)";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql, new String[]{"ID_MENSAGEM"})) {

            ps.setLong(1, m.getIdUsuario());
            ps.setLong(2, m.getIdEstacao());
            ps.setString(3, m.getTexto());
            ps.setTimestamp(4, Timestamp.valueOf(m.getDataHora()));
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    m.setId(rs.getLong(1));
                }
            }
        }
    }

    // JOIN com a tabela de usuário para trazer o nome de quem escreveu
    public List<MensagemChat> listarPorEstacao(Long idEstacao) throws SQLException {
        String sql = "SELECT M.*, U.NM_USUARIO FROM TB_MTR_MENSAGEM_CHAT M "
                + "JOIN TB_MTR_USUARIO U ON U.ID_USUARIO = M.ID_USUARIO "
                + "WHERE M.ID_ESTACAO = ? ORDER BY M.DT_ENVIO";
        List<MensagemChat> lista = new ArrayList<>();

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, idEstacao);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(montarMensagem(rs));
                }
            }
        }
        return lista;
    }

    public MensagemChat buscarPorId(Long id) throws SQLException {
        String sql = "SELECT M.*, U.NM_USUARIO FROM TB_MTR_MENSAGEM_CHAT M "
                + "JOIN TB_MTR_USUARIO U ON U.ID_USUARIO = M.ID_USUARIO "
                + "WHERE M.ID_MENSAGEM = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? montarMensagem(rs) : null;
            }
        }
    }

    public void atualizarTexto(Long id, String texto) throws SQLException {
        String sql = "UPDATE TB_MTR_MENSAGEM_CHAT SET DS_TEXTO = ? WHERE ID_MENSAGEM = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setString(1, texto);
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void deletar(Long id) throws SQLException {
        String sql = "DELETE FROM TB_MTR_MENSAGEM_CHAT WHERE ID_MENSAGEM = ?";

        try (Connection conexao = connectionFactory.obterConexao();
             PreparedStatement ps = conexao.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    // Converte a linha atual do ResultSet em um objeto MensagemChat
    private MensagemChat montarMensagem(ResultSet rs) throws SQLException {
        return new MensagemChat(
                rs.getLong("ID_MENSAGEM"),
                rs.getLong("ID_USUARIO"),
                rs.getLong("ID_ESTACAO"),
                rs.getString("NM_USUARIO"),
                rs.getString("DS_TEXTO"),
                rs.getTimestamp("DT_ENVIO").toLocalDateTime()
        );
    }
}