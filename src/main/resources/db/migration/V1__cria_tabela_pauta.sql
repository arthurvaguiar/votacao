CREATE TABLE pauta
(
    id        BIGSERIAL PRIMARY KEY,
    titulo    VARCHAR(150) NOT NULL,
    descricao VARCHAR(1000),
    criada_em TIMESTAMPTZ  NOT NULL
);