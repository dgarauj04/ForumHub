package com.alura.forumhub.domain.topico;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizacaoTopico(
        @NotNull(message = "ID é obrigatorio para atualizar")
        Long id,
        String titulo,
        String mensagem,
        StatusTopico status
) {
}
