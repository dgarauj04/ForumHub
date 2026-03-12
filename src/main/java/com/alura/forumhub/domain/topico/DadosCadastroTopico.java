package com.alura.forumhub.domain.topico;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record DadosCadastroTopico (
    @NotBlank(message = "Titulo é obrigatorio")
    String titulo,

    @NotBlank(message = "Mensagem é obrigatória")
    String mensagem,

    @NotNull(message = "ID do autor é obrigatório")
    Long autorId,

    @NotNull(message = "ID do curso é obrigatório")
    Long cursoId
) {}
