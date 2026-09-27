WITH p AS (
INSERT INTO pauta (titulo, criada_em)
VALUES ('Pauta com 500 mil votos', now())
    RETURNING id
    ), s AS (
INSERT INTO sessao (pauta_id, abertura, fechamento)
SELECT id, now() - interval '2 hours', now() - interval '1 hour' FROM p
    RETURNING pauta_id
    )
INSERT INTO voto (pauta_id, associado_id, opcao, registrado_em)
SELECT s.pauta_id, 'massa-' || g,
       CASE WHEN random() < 0.5 THEN 'SIM' ELSE 'NAO' END,
       now() - interval '90 minutes'
FROM s, generate_series(1, 500000) g;

VACUUM ANALYZE voto;

SELECT id FROM pauta WHERE titulo = 'Pauta com 500 mil votos';