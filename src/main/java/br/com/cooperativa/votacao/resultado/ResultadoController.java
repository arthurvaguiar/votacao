package br.com.cooperativa.votacao.resultado;

import br.com.cooperativa.votacao.resultado.dto.ResultadoResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/resultado")
public class ResultadoController {

    private final ResultadoService service;

    public ResultadoController(ResultadoService service) {
        this.service = service;
    }

    @GetMapping
    public ResultadoResponse resultado(@PathVariable Long pautaId) {
        return service.apurar(pautaId);
    }
}