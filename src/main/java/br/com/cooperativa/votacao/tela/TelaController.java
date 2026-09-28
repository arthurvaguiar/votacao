package br.com.cooperativa.votacao.tela;

import br.com.cooperativa.votacao.pauta.dto.CriarPautaRequest;
import br.com.cooperativa.votacao.sessao.dto.AbrirSessaoRequest;
import br.com.cooperativa.votacao.tela.dto.IdentificacaoAssociadoRequest;
import br.com.cooperativa.votacao.tela.modelo.Tela;
import br.com.cooperativa.votacao.voto.dto.RegistrarVotoRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Telas mobile", description = "Telas FORMULARIO/SELECAO consumidas pelo app mobile")
@RestController
@RequestMapping("/api/v1/telas")
public class TelaController {

    private final TelaService service;

    public TelaController(TelaService service) {
        this.service = service;
    }

    @Operation(summary = "Tela inicial (ponto de entrada do app)")
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public Tela inicio() {
        return service.inicio();
    }

    @Operation(summary = "Lista de pautas para seleção")
    @PostMapping("/pautas")
    public Tela listarPautas() {
        return service.listarPautas();
    }

    @Operation(summary = "Formulário de cadastro de pauta")
    @PostMapping("/pautas/nova")
    public Tela formularioNovaPauta() {
        return service.formularioNovaPauta();
    }

    @Operation(summary = "Cadastra a pauta e exibe a confirmação")
    @PostMapping("/pautas/cadastrar")
    public Tela cadastrarPauta(@Valid @RequestBody CriarPautaRequest request) {
        return service.cadastrarPauta(request.titulo(), request.descricao());
    }

    @Operation(summary = "Ações da pauta: abrir sessão, votar e ver resultado")
    @PostMapping("/pautas/{pautaId}")
    public Tela detalharPauta(@PathVariable Long pautaId) {
        return service.detalharPauta(pautaId);
    }

    @Operation(summary = "Formulário de abertura de sessão")
    @PostMapping("/pautas/{pautaId}/sessao/nova")
    public Tela formularioAbrirSessao(@PathVariable Long pautaId) {
        return service.formularioAbrirSessao(pautaId);
    }

    @Operation(summary = "Abre a sessão e exibe a confirmação")
    @PostMapping("/pautas/{pautaId}/sessao/abrir")
    public Tela abrirSessao(@PathVariable Long pautaId,
                            @Valid @RequestBody(required = false) AbrirSessaoRequest request) {
        var duracao = request == null ? null : request.duracaoEmMinutos();
        return service.abrirSessao(pautaId, duracao);
    }

    @Operation(summary = "Formulário de identificação do associado (CPF)")
    @PostMapping("/pautas/{pautaId}/voto/novo")
    public Tela formularioIdentificacao(@PathVariable Long pautaId) {
        return service.formularioIdentificacao(pautaId);
    }

    @Operation(summary = "Opções de voto (Sim/Não) com o CPF no body")
    @PostMapping("/pautas/{pautaId}/voto/opcoes")
    public Tela opcoesVoto(@PathVariable Long pautaId,
                           @Valid @RequestBody IdentificacaoAssociadoRequest request) {
        return service.opcoesVoto(pautaId, request.associadoId());
    }

    @Operation(summary = "Registra o voto e exibe a confirmação")
    @PostMapping("/pautas/{pautaId}/voto/registrar")
    public Tela registrarVoto(@PathVariable Long pautaId, @Valid @RequestBody RegistrarVotoRequest request) {
        return service.registrarVoto(pautaId, request.associadoId(), request.voto());
    }

    @Operation(summary = "Resultado da votação, com botão para atualizar")
    @PostMapping("/pautas/{pautaId}/resultado")
    public Tela resultado(@PathVariable Long pautaId) {
        return service.resultado(pautaId);
    }
}