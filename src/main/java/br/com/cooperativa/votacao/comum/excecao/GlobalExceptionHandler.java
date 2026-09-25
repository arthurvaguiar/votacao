package br.com.cooperativa.votacao.comum.excecao;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ProblemDetail tratarNaoEncontrado(RecursoNaoEncontradoException ex) {
        return erroDeNegocio(HttpStatus.NOT_FOUND, "Recurso não encontrado", ex);
    }

    @ExceptionHandler(ConflitoException.class)
    public ProblemDetail tratarConflito(ConflitoException ex) {
        return erroDeNegocio(HttpStatus.CONFLICT, "Conflito", ex);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ProblemDetail tratarRegraNegocio(RegraNegocioException ex) {
        return erroDeNegocio(HttpStatus.UNPROCESSABLE_CONTENT, "Regra de negócio violada", ex);
    }

    @ExceptionHandler(Exception.class)
    public ProblemDetail tratarErroInesperado(Exception ex) {
        log.error("Erro inesperado", ex);
        return problema(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        var erros = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> new ErroCampo(e.getField(), e.getDefaultMessage()))
                .toList();
        log.warn("Requisição com campos inválidos: {}", erros);

        var problema = problema(HttpStatus.BAD_REQUEST, "Dados inválidos", "Um ou mais campos estão inválidos");
        problema.setProperty("erros", erros);
        return ResponseEntity.badRequest().body(problema);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers,
                                                                  HttpStatusCode status,
                                                                  WebRequest request) {
        var causa = ex.getMostSpecificCause();
        // IllegalArgumentException vem das nossas validações (ex.: OpcaoVoto); demais causas não são expostas
        var detalhe = causa instanceof IllegalArgumentException
                ? causa.getMessage()
                : "Corpo da requisição inválido ou mal formatado";
        log.warn("Corpo da requisição ilegível: {}", causa.getMessage());

        return ResponseEntity.badRequest().body(problema(HttpStatus.BAD_REQUEST, "Requisição inválida", detalhe));
    }

    private ProblemDetail erroDeNegocio(HttpStatus status, String titulo, RuntimeException ex) {
        log.warn("{}: {}", titulo, ex.getMessage());
        return problema(status, titulo, ex.getMessage());
    }

    private ProblemDetail problema(HttpStatus status, String titulo, String detalhe) {
        var problema = ProblemDetail.forStatusAndDetail(status, detalhe);
        problema.setTitle(titulo);
        problema.setProperty("timestamp", Instant.now());
        return problema;
    }
}