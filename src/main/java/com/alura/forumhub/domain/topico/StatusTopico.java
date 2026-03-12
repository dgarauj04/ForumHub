package com.alura.forumhub.domain.topico;

public enum StatusTopico {
    ABERTO,RESPONDIDO,FECHADO,SOLUCIONADO;

    public boolean podeTransicionarPara(StatusTopico novo) {
        return switch (this) {
            case ABERTO -> novo == RESPONDIDO || novo == FECHADO;
            case RESPONDIDO -> novo == SOLUCIONADO || novo == FECHADO;
            case FECHADO, SOLUCIONADO -> false;
        };
    }
}
