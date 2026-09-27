package br.com.cooperativa.votacao.tela;

import br.com.cooperativa.votacao.pauta.dto.CriarPautaRequest;
import br.com.cooperativa.votacao.sessao.dto.AbrirSessaoRequest;
import br.com.cooperativa.votacao.tela.dto.IdentificacaoAssociadoRequest;
import br.com.cooperativa.votacao.tela.modelo.Tela;
import br.com.cooperativa.votacao.voto.dto.RegistrarVotoRequest;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/telas")
public class TelaController {

    private final TelaService service;

    public TelaController(TelaService service) {
        this.service = service;
    }

    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public Tela inicio() {
        return service.inicio();
    }

    @PostMapping("/pautas")
    public Tela listarPautas() {
        return service.listarPautas();
    }

    @PostMapping("/pautas/nova")
    public Tela formularioNovaPauta() {
        return service.formularioNovaPauta();
    }

    @PostMapping("/pautas/cadastrar")
    public Tela cadastrarPauta(@Valid @RequestBody CriarPautaRequest request) {
        return service.cadastrarPauta(request.titulo(), request.descricao());
    }

    @PostMapping("/pautas/{pautaId}")
    public Tela detalharPauta(@PathVariable Long pautaId) {
        return service.detalharPauta(pautaId);
    }

    @PostMapping("/pautas/{pautaId}/sessao/nova")
    public Tela formularioAbrirSessao(@PathVariable Long pautaId) {
        return service.formularioAbrirSessao(pautaId);
    }

    @PostMapping("/pautas/{pautaId}/sessao/abrir")
    public Tela abrirSessao(@PathVariable Long pautaId,
                            @Valid @RequestBody(required = false) AbrirSessaoRequest request) {
        var duracao = request == null ? null : request.duracaoEmMinutos();
        return service.abrirSessao(pautaId, duracao);
    }

    @PostMapping("/pautas/{pautaId}/voto/novo")
    public Tela formularioIdentificacao(@PathVariable Long pautaId) {
        return service.formularioIdentificacao(pautaId);
    }

    @PostMapping("/pautas/{pautaId}/voto/opcoes")
    public Tela opcoesVoto(@PathVariable Long pautaId,
                           @Valid @RequestBody IdentificacaoAssociadoRequest request) {
        return service.opcoesVoto(pautaId, request.associadoId());
    }

    @PostMapping("/pautas/{pautaId}/voto/registrar")
    public Tela registrarVoto(@PathVariable Long pautaId, @Valid @RequestBody RegistrarVotoRequest request) {
        return service.registrarVoto(pautaId, request.associadoId(), request.voto());
    }

    @PostMapping("/pautas/{pautaId}/resultado")
    public Tela resultado(@PathVariable Long pautaId) {
        return service.resultado(pautaId);
    }
}