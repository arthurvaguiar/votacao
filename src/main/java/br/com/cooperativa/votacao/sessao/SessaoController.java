package br.com.cooperativa.votacao.sessao;


import br.com.cooperativa.votacao.sessao.dto.AbrirSessaoRequest;
import br.com.cooperativa.votacao.sessao.dto.SessaoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Sessões", description = "Abertura de sessões de votação")
@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/sessao")
public class SessaoController {

    private final SessaoService service;

    public SessaoController(SessaoService service) {
        this.service = service;
    }

    @Operation(summary = "Abre a sessão de votação da pauta",
            description = "Duração opcional em minutos; sem body, a sessão fica aberta por 1 minuto. Cada pauta tem uma única sessão.")    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessaoResponse abrir(@PathVariable Long pautaId,
                                @Valid @RequestBody(required = false) AbrirSessaoRequest request) {
        var duracao = request == null ? null : request.duracaoEmMinutos();
        return SessaoResponse.de(service.abrir(pautaId, duracao));
    }
}