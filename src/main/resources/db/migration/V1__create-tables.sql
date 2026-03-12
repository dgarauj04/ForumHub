CREATE TABLE cursos (
    id        BIGSERIAL    PRIMARY KEY,
    nome      VARCHAR(200) NOT NULL,
    categoria VARCHAR(150) NOT NULL
);

CREATE TABLE perfis (
    id   BIGSERIAL    PRIMARY KEY,
    nome VARCHAR(100) NOT NULL
);

CREATE TABLE usuarios (
    id    BIGSERIAL    PRIMARY KEY,
    nome  VARCHAR(200) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    senha VARCHAR(255) NOT NULL
);

CREATE TABLE usuarios_perfis (
    usuario_id BIGINT NOT NULL REFERENCES usuarios(id),
    perfil_id  BIGINT NOT NULL REFERENCES perfis(id),
    PRIMARY KEY (usuario_id, perfil_id)
);

CREATE TABLE topicos (
    id           BIGSERIAL    PRIMARY KEY,
    titulo       VARCHAR(200) NOT NULL,
    mensagem     TEXT         NOT NULL,
    data_criacao TIMESTAMP    NOT NULL DEFAULT NOW(),
    status       VARCHAR(50)  NOT NULL DEFAULT 'ABERTO',
    autor_id     BIGINT       NOT NULL REFERENCES usuarios(id),
    curso_id     BIGINT       NOT NULL REFERENCES cursos(id),
    CONSTRAINT uq_topicos_titulo_mensagem UNIQUE (titulo, mensagem)
);

CREATE TABLE respostas (
    id           BIGSERIAL PRIMARY KEY,
    mensagem     TEXT      NOT NULL,
    topico_id    BIGINT    NOT NULL REFERENCES topicos(id),
    data_criacao TIMESTAMP NOT NULL DEFAULT NOW(),
    autor_id     BIGINT    NOT NULL REFERENCES usuarios(id),
    solucao      BOOLEAN   NOT NULL DEFAULT FALSE
);