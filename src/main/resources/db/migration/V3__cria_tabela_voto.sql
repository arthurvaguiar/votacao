CREATE TABLE voto
(
    id            BIGSERIAL PRIMARY KEY,
    pauta_id      BIGINT      NOT NULL REFERENCES pauta (id),
    associado_id  VARCHAR(50) NOT NULL,
    opcao         VARCHAR(3)  NOT NULL CHECK (opcao IN ('SIM', 'NAO')),
    registrado_em TIMESTAMPTZ NOT NULL,
    CONSTRAINT uk_voto_pauta_associado UNIQUE (pauta_id, associado_id)
);