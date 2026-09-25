package br.com.cooperativa.votacao.voto;


import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "voto")
public class Voto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "pauta_id", nullable = false)
    private Long pautaId;

    @Column(name = "associado_id", nullable = false, length = 50)
    private String associadoId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 3)
    private OpcaoVoto opcao;

    @Column(name = "registrado_em", nullable = false)
    private Instant registradoEm;

    protected Voto() {
    }

    public Voto(Long pautaId, String associadoId, OpcaoVoto opcao, Instant registradoEm) {
        this.pautaId = pautaId;
        this.associadoId = associadoId;
        this.opcao = opcao;
        this.registradoEm = registradoEm;
    }

    public Long getId() { return id; }
    public Long getPautaId() { return pautaId; }
    public String getAssociadoId() { return associadoId; }
    public OpcaoVoto getOpcao() { return opcao; }
    public Instant getRegistradoEm() { return registradoEm; }
}