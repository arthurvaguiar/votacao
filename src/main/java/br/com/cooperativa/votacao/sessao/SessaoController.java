package br.com.cooperativa.votacao.sessao;


import br.com.cooperativa.votacao.sessao.dto.AbrirSessaoRequest;
import br.com.cooperativa.votacao.sessao.dto.SessaoResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/sessao")
public class SessaoController {

    private final SessaoService service;

    public SessaoController(SessaoService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SessaoResponse abrir(@PathVariable Long pautaId,
                                @Valid @RequestBody(required = false) AbrirSessaoRequest request) {
        var duracao = request == null ? null : request.duracaoEmMinutos();
        return SessaoResponse.de(service.abrir(pautaId, duracao));
    }
}