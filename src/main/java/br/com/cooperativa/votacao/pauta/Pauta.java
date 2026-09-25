package br.com.cooperativa.votacao.pauta;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "pauta")
public class Pauta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String titulo;

    @Column(length = 1000)
    private String descricao;

    @Column(name = "criada_em", nullable = false)
    private Instant criadaEm;

    protected Pauta() {
    }

    public Pauta(String titulo, String descricao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.criadaEm = Instant.now();
    }

    public Long getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getDescricao() { return descricao; }
    public Instant getCriadaEm() { return criadaEm; }
}