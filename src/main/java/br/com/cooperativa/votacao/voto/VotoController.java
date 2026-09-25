package br.com.cooperativa.votacao.voto;

import br.com.cooperativa.votacao.voto.dto.RegistrarVotoRequest;
import br.com.cooperativa.votacao.voto.dto.VotoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/votos")
public class VotoController {

    private final VotoService service;

    public VotoController(VotoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VotoResponse votar(@PathVariable Long pautaId, @Valid @RequestBody RegistrarVotoRequest request) {
        return VotoResponse.de(service.registrar(pautaId, request.associadoId(), request.voto()));
    }
}