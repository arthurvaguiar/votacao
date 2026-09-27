package br.com.cooperativa.votacao.tela;

import br.com.cooperativa.votacao.comum.excecao.ConflitoException;
import br.com.cooperativa.votacao.comum.excecao.IntegracaoIndisponivelException;
import br.com.cooperativa.votacao.comum.excecao.RecursoNaoEncontradoException;
import br.com.cooperativa.votacao.comum.excecao.RegraNegocioException;
import br.com.cooperativa.votacao.tela.modelo.Tela;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice(assignableTypes = TelaController.class)
public class TelaExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(TelaExceptionHandler.class);

    private final TelaService telaService;

    public TelaExceptionHandler(TelaService telaService) {
        this.telaService = telaService;
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<Tela> naoEncontrado(RecursoNaoEncontradoException ex) {
        return erroDeNegocio(HttpStatus.NOT_FOUND, ex);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<Tela> conflito(ConflitoException ex) {
        return erroDeNegocio(HttpStatus.CONFLICT, ex);
    }

    @ExceptionHandler(RegraNegocioException.class)
    public ResponseEntity<Tela> regraNegocio(RegraNegocioException ex) {
        return erroDeNegocio(HttpStatus.UNPROCESSABLE_CONTENT, ex);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Tela> validacao(MethodArgumentNotValidException ex) {
        var mensagem = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + " " + e.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.warn("Tela com campos inválidos: {}", mensagem);
        return resposta(HttpStatus.BAD_REQUEST, "Verifique os dados informados: " + mensagem);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Tela> corpoInvalido(HttpMessageNotReadableException ex) {
        var causa = ex.getMostSpecificCause();
        log.warn("Corpo da requisição de tela ilegível: {}", causa.getMessage());
        var mensagem = causa instanceof IllegalArgumentException
                ? causa.getMessage()
                : "Não foi possível ler os dados enviados.";
        return resposta(HttpStatus.BAD_REQUEST, mensagem);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Tela> inesperado(Exception ex) {
        log.error("Erro inesperado em tela", ex);
        return resposta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado. Tente novamente mais tarde.");
    }

    @ExceptionHandler(IntegracaoIndisponivelException.class)
    public ResponseEntity<Tela> integracaoIndisponivel(IntegracaoIndisponivelException ex) {
        return erroDeNegocio(HttpStatus.SERVICE_UNAVAILABLE, ex);
    }

    private ResponseEntity<Tela> erroDeNegocio(HttpStatus status, RuntimeException ex) {
        log.warn("Erro de negócio em tela: {}", ex.getMessage());
        return resposta(status, ex.getMessage());
    }

    private ResponseEntity<Tela> resposta(HttpStatus status, String mensagem) {
        return ResponseEntity.status(status).body(telaService.erro(mensagem));
    }
}