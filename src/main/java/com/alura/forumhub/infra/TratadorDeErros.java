package com.alura.forumhub.infra;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.naming.AuthenticationException;
import java.nio.file.AccessDeniedException;
import java.time.LocalDateTime;
import java.util.List;

@RestControllerAdvice
public class TratadorDeErros {
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<DadosErro> tratarErro404(EntityNotFoundException ex) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(new DadosErro(
                        HttpStatus.NOT_FOUND.value(),
                        "Recurso não encontrado",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<DadosErroValidacao> tratarErro400(
            MethodArgumentNotValidException ex) {

        var campos = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(CampoInvalido::new)
                .toList();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new DadosErroValidacao(
                        HttpStatus.BAD_REQUEST.value(),
                        "Erro de validação nos campos enviados",
                        campos
                ));
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<DadosErro> tratarErroNegocio(RuntimeException ex) {
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(new DadosErro(
                        HttpStatus.BAD_REQUEST.value(),
                        "Erro na requisição",
                        ex.getMessage()
                ));
    }

    @ExceptionHandler({BadCredentialsException.class, AuthenticationException.class})
    public ResponseEntity<DadosErro> tratarErroAutenticacao() {
        return ResponseEntity
                .status(HttpStatus.UNAUTHORIZED)
                .body(new DadosErro(
                        HttpStatus.UNAUTHORIZED.value(),
                        "Falha na autenticação",
                        "Login ou senha inválidos"
                ));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<DadosErro> tratarErroAcessoNegado() {
        return ResponseEntity
                .status(HttpStatus.FORBIDDEN)
                .body(new DadosErro(
                        HttpStatus.FORBIDDEN.value(),
                        "Acesso negado",
                        "Você não tem permissão para acessar este recurso"
                ));
    }

    public record DadosErro(
            int           status,
            String        erro,
            String        mensagem,
            LocalDateTime timestamp
    ) {
        public DadosErro(int status, String erro, String mensagem) {
            this(status, erro, mensagem, LocalDateTime.now());
        }
    }

    public record DadosErroValidacao(
            int              status,
            String           erro,
            List<CampoInvalido> campos,
            LocalDateTime    timestamp
    ) {
        public DadosErroValidacao(int status, String erro, List<CampoInvalido> campos) {
            this(status, erro, campos, LocalDateTime.now());
        }
    }

    public record CampoInvalido(String campo, String mensagem) {
        public CampoInvalido(FieldError error) {
            this(error.getField(), error.getDefaultMessage());
        }
    }
}
