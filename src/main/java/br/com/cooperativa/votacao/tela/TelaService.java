package br.com.cooperativa.votacao.tela;

import br.com.cooperativa.votacao.pauta.PautaService;
import br.com.cooperativa.votacao.resultado.ResultadoService;
import br.com.cooperativa.votacao.sessao.SessaoService;
import br.com.cooperativa.votacao.tela.modelo.*;
import br.com.cooperativa.votacao.voto.OpcaoVoto;
import br.com.cooperativa.votacao.voto.VotoService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Service
public class TelaService {

    private static final int LIMITE_PAUTAS = 20;

    private final PautaService pautaService;
    private final SessaoService sessaoService;
    private final VotoService votoService;
    private final ResultadoService resultadoService;
    private final TelaUrls urls;

    public TelaService(PautaService pautaService, SessaoService sessaoService, VotoService votoService,
                       ResultadoService resultadoService, TelaUrls urls) {
        this.pautaService = pautaService;
        this.sessaoService = sessaoService;
        this.votoService = votoService;
        this.resultadoService = resultadoService;
        this.urls = urls;
    }

    public Tela inicio() {
        return new TelaSelecao("Assembleia", List.of(
                new ItemSelecao("Cadastrar pauta", urls.novaPauta()),
                new ItemSelecao("Pautas", urls.pautas())));
    }

    public Tela listarPautas() {
        var pageable = PageRequest.of(0, LIMITE_PAUTAS, Sort.by(Sort.Direction.DESC, "criadaEm"));
        var pautas = pautaService.listar(pageable).getContent();

        if (pautas.isEmpty()) {
            return mensagem("Pautas", "Nenhuma pauta cadastrada.",
                    new Botao("Cadastrar pauta", urls.novaPauta()));
        }
        var itens = pautas.stream()
                .map(p -> new ItemSelecao(p.getTitulo(), urls.pauta(p.getId())))
                .toList();
        return new TelaSelecao("Pautas", itens);
    }

    public Tela formularioNovaPauta() {
        return new TelaFormulario("Nova pauta",
                List.of(Campo.inputTexto("titulo", "Título", ""),
                        Campo.inputTexto("descricao", "Descrição", "")),
                new Botao("Cadastrar", urls.cadastrarPauta()),
                new Botao("Cancelar", urls.inicio()));
    }

    public Tela cadastrarPauta(String titulo, String descricao) {
        var pauta = pautaService.criar(titulo, descricao);
        return mensagem("Pauta cadastrada",
                "Pauta \"" + pauta.getTitulo() + "\" cadastrada com sucesso.",
                new Botao("Abrir sessão", urls.novaSessao(pauta.getId())));
    }

    public Tela detalharPauta(Long pautaId) {
        var pauta = pautaService.buscar(pautaId);
        return new TelaSelecao(pauta.getTitulo(), List.of(
                new ItemSelecao("Abrir sessão de votação", urls.novaSessao(pautaId)),
                new ItemSelecao("Votar", urls.novoVoto(pautaId)),
                new ItemSelecao("Ver resultado", urls.resultado(pautaId))));
    }

    public Tela formularioAbrirSessao(Long pautaId) {
        var pauta = pautaService.buscar(pautaId);
        return new TelaFormulario("Abrir sessão",
                List.of(Campo.texto(pauta.getTitulo()),
                        Campo.inputNumero("duracaoEmMinutos", "Duração (minutos)", 1)),
                new Botao("Abrir", urls.abrirSessao(pautaId)),
                new Botao("Cancelar", urls.pauta(pautaId)));
    }

    public Tela abrirSessao(Long pautaId, Integer duracaoEmMinutos) {
        var sessao = sessaoService.abrir(pautaId, duracaoEmMinutos);
        var minutos = Duration.between(sessao.getAbertura(), sessao.getFechamento()).toMinutes();
        return mensagem("Sessão aberta", "Sessão aberta por " + minutos + " minuto(s).",
                new Botao("Votar", urls.novoVoto(pautaId)));
    }

    public Tela formularioIdentificacao(Long pautaId) {
        var pauta = pautaService.buscar(pautaId);
        return new TelaFormulario("Votar",
                List.of(Campo.texto(pauta.getTitulo()),
                        Campo.inputTexto("associadoId", "Identificação do associado", "")),
                new Botao("Continuar", urls.opcoesVoto(pautaId)),
                new Botao("Cancelar", urls.pauta(pautaId)));
    }

    public Tela opcoesVoto(Long pautaId, String associadoId) {
        return new TelaSelecao("Seu voto", List.of(
                opcao("Sim", pautaId, associadoId, OpcaoVoto.SIM),
                opcao("Não", pautaId, associadoId, OpcaoVoto.NAO)));
    }

    public Tela registrarVoto(Long pautaId, String associadoId, OpcaoVoto voto) {
        votoService.registrar(pautaId, associadoId, voto);
        return mensagem("Voto registrado", "Seu voto foi registrado com sucesso.",
                new Botao("Ver resultado", urls.resultado(pautaId)));
    }

    public Tela resultado(Long pautaId) {
        var resultado = resultadoService.apurar(pautaId);
        return new TelaFormulario("Resultado",
                List.of(Campo.texto(resultado.titulo()),
                        Campo.texto("Situação: " + resultado.status().getDescricao()),
                        Campo.texto("Sim: " + resultado.votosSim()),
                        Campo.texto("Não: " + resultado.votosNao()),
                        Campo.texto("Total de votos: " + resultado.totalVotos())),
                new Botao("Atualizar", urls.resultado(pautaId)),
                new Botao("Início", urls.inicio()));
    }

    public Tela erro(String mensagem) {
        return mensagem("Atenção", mensagem, null);
    }

    private ItemSelecao opcao(String texto, Long pautaId, String associadoId, OpcaoVoto voto) {
        return new ItemSelecao(texto, urls.registrarVoto(pautaId),
                Map.of("associadoId", associadoId, "voto", voto.name()));
    }

    private Tela mensagem(String titulo, String texto, Botao acao) {
        var inicio = new Botao("Início", urls.inicio());
        return acao == null
                ? new TelaFormulario(titulo, List.of(Campo.texto(texto)), inicio, null)
                : new TelaFormulario(titulo, List.of(Campo.texto(texto)), acao, inicio);
    }
}