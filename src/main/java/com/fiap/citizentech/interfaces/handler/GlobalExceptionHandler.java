package com.fiap.citizentech.interfaces.handler;

import com.fiap.citizentech.domain.exception.RecursoNaoEncontradoException;
import com.fiap.citizentech.domain.exception.RegraNegocioException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Camada de tratamento de exceções.
 * Qualquer exceção lançada em um Controller (ou no BO/DAO chamado por ele)
 * cai aqui e vira uma resposta JSON padronizada com o status HTTP correto.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - registro não existe
    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResponse> tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return montar(HttpStatus.NOT_FOUND, ex.getMessage(), List.of());
    }

    // 400 - regra de negócio violada
    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<ErroResponse> tratarRegraNegocio(RegraNegocioException ex) {
        return montar(HttpStatus.BAD_REQUEST, ex.getMessage(), List.of());
    }

    // 400 - falhou nas anotações @NotBlank, @Email, @Size... do model
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResponse> tratarValidacao(MethodArgumentNotValidException ex) {
        List<String> detalhes = ex.getBindingResult().getFieldErrors().stream()
                .map(erro -> erro.getField() + ": " + erro.getDefaultMessage())
                .toList();
        return montar(HttpStatus.BAD_REQUEST, "Dados inválidos", detalhes);
    }

    // 400 - JSON mal formatado ou com tipo errado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResponse> tratarJsonInvalido(HttpMessageNotReadableException ex) {
        return montar(HttpStatus.BAD_REQUEST, "JSON inválido ou mal formatado", List.of());
    }

    // 400 - parâmetro na URL com tipo errado (ex: /usuarios/abc)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResponse> tratarTipoParametro(MethodArgumentTypeMismatchException ex) {
        String msg = "Valor inválido para o parâmetro '" + ex.getName() + "'";
        return montar(HttpStatus.BAD_REQUEST, msg, List.of());
    }

    // 400 - faltou um ?parametro obrigatório
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResponse> tratarParametroFaltando(MissingServletRequestParameterException ex) {
        String msg = "Parâmetro obrigatório ausente: " + ex.getParameterName();
        return montar(HttpStatus.BAD_REQUEST, msg, List.of());
    }

    // 404 - URL que não existe na API
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResponse> tratarUrlInexistente(NoResourceFoundException ex) {
        return montar(HttpStatus.NOT_FOUND, "Endpoint não encontrado", List.of());
    }

    // 405 - método HTTP errado (ex: DELETE em /usuarios/login)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResponse> tratarMetodo(HttpRequestMethodNotSupportedException ex) {
        String msg = "Método " + ex.getMethod() + " não suportado aqui";
        return montar(HttpStatus.METHOD_NOT_ALLOWED, msg, List.of());
    }

    // 500 - erro no banco de dados
    @ExceptionHandler(SQLException.class)
    public ResponseEntity<ErroResponse> tratarBanco(SQLException ex) {
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro ao acessar o banco de dados",
                List.of(ex.getMessage()));
    }

    // 500 - qualquer outro erro inesperado
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResponse> tratarGenerico(Exception ex) {
        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno no servidor", List.of());
    }

    // Monta a resposta padrão usada por todos os métodos acima
    private ResponseEntity<ErroResponse> montar(HttpStatus status, String mensagem, List<String> detalhes) {
        ErroResponse corpo = new ErroResponse(
                LocalDateTime.now(), status.value(), status.getReasonPhrase(), mensagem, detalhes);
        return ResponseEntity.status(status).body(corpo);
    }
}