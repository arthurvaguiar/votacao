package br.com.cooperativa.votacao.sessao;


import br.com.cooperativa.votacao.pauta.Pauta;
import jakarta.persistence.*;
import java.time.Duration;
import java.time.Instant;

@Entity
@Table(name = "sessao")
public class Sessao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pauta_id", nullable = false, unique = true)
    private Pauta pauta;

    @Column(nullable = false)
    private Instant abertura;

    @Column(nullable = false)
    private Instant fechamento;

    protected Sessao() {
    }

    public Sessao(Pauta pauta, Instant abertura, Duration duracao) {
        this.pauta = pauta;
        this.abertura = abertura;
        this.fechamento = abertura.plus(duracao);
    }

    public boolean estaAberta(Instant agora) {
        return !agora.isBefore(abertura) && agora.isBefore(fechamento);
    }

    public Long getId() { return id; }
    public Pauta getPauta() { return pauta; }
    public Instant getAbertura() { return abertura; }
    public Instant getFechamento() { return fechamento; }
}