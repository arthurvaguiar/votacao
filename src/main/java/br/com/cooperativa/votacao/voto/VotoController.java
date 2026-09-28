package br.com.cooperativa.votacao.voto;

import br.com.cooperativa.votacao.voto.dto.RegistrarVotoRequest;
import br.com.cooperativa.votacao.voto.dto.VotoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Votos", description = "Registro de votos dos associados")
@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/votos")
public class VotoController {

    private final VotoService service;

    public VotoController(VotoService service) {
        this.service = service;
    }

    @Operation(summary = "Registra o voto (Sim/Não) de um associado",
            description = "O associadoId é o CPF. Exige sessão aberta e permite um voto por associado por pauta.")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VotoResponse votar(@PathVariable Long pautaId, @Valid @RequestBody RegistrarVotoRequest request) {
        return VotoResponse.de(service.registrar(pautaId, request.associadoId(), request.voto()));
    }
}