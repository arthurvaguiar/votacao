package br.com.cooperativa.votacao.pauta;


import br.com.cooperativa.votacao.pauta.dto.CriarPautaRequest;
import br.com.cooperativa.votacao.pauta.dto.PautaResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

@Tag(name = "Pautas", description = "Cadastro e consulta de pautas")
@RestController
@RequestMapping("/api/v1/pautas")
public class PautaController {

    private final PautaService service;

    public PautaController(PautaService service) {
        this.service = service;
    }

    @Operation(summary = "Cadastra pauta")
    @PostMapping
    public ResponseEntity<PautaResponse> criar(@Valid @RequestBody CriarPautaRequest request,
                                               UriComponentsBuilder uriBuilder) {
        var pauta = service.criar(request.titulo(), request.descricao());
        var uri = uriBuilder.path("/api/v1/pautas/{id}").buildAndExpand(pauta.getId()).toUri();
        return ResponseEntity.created(uri).body(PautaResponse.de(pauta));
    }

    @Operation(summary = "Busca pauta por id")
    @GetMapping("/{id}")
    public PautaResponse buscar(@PathVariable Long id) {
        return PautaResponse.de(service.buscar(id));
    }

    @Operation(summary = "Lista as pautas, das mais recentes para as mais antigas (paginado)")
    @GetMapping
    public Page<PautaResponse> listar(
            @PageableDefault(size = 20, sort = "criadaEm", direction = Sort.Direction.DESC) Pageable pageable) {
        return service.listar(pageable).map(PautaResponse::de);
    }
}