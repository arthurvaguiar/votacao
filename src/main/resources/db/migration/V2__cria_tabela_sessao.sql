CREATE TABLE sessao
(
    id         BIGSERIAL PRIMARY KEY,
    pauta_id   BIGINT      NOT NULL UNIQUE REFERENCES pauta (id),
    abertura   TIMESTAMPTZ NOT NULL,
    fechamento TIMESTAMPTZ NOT NULL
);