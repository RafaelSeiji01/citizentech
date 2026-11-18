package com.fiap.citizentech.infrastructure.database;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
/**
 * Fábrica de conexões com o Oracle.
 * Os dados vêm do application.properties (que lê as variáveis de ambiente).
 */
@Component
public class ConnectionFactory {
    @Value("${db.url}")
    private String url;

    @Value("${db.user}")
    private String usuario;

    @Value("${db.password}")
    private String senha;

    public Connection obterConexao() throws SQLException {
        return DriverManager.getConnection(url, usuario, senha);
    }
}