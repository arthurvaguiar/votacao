package br.com.cooperativa.votacao.resultado;

import br.com.cooperativa.votacao.resultado.dto.ResultadoResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Resultado", description = "Apuração da votação")
@RestController
@RequestMapping("/api/v1/pautas/{pautaId}/resultado")
public class ResultadoController {

    private final ResultadoService service;

    public ResultadoController(ResultadoService service) {
        this.service = service;
    }

    @Operation(summary = "Apura o resultado da votação da pauta",
            description = "EM_ANDAMENTO com contagem parcial enquanto a sessão está aberta; APROVADA, REPROVADA ou EMPATE após o encerramento.")
    @GetMapping
    public ResultadoResponse resultado(@PathVariable Long pautaId) {
        return service.apurar(pautaId);
    }
}