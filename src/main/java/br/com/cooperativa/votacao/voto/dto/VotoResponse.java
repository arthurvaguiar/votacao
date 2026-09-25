package br.com.cooperativa.votacao.voto.dto;

import br.com.cooperativa.votacao.voto.OpcaoVoto;
import br.com.cooperativa.votacao.voto.Voto;
import java.time.Instant;

public record VotoResponse(Long id, Long pautaId, String associadoId, OpcaoVoto voto, Instant registradoEm) {

    public static VotoResponse de(Voto voto) {
        return new VotoResponse(voto.getId(), voto.getPautaId(), voto.getAssociadoId(),
                voto.getOpcao(), voto.getRegistradoEm());
    }
}