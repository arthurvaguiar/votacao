package br.com.cooperativa.votacao.pauta;

import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PautaService {

    private static final Logger log = LoggerFactory.getLogger(PautaService.class);

    private final PautaRepository repository;

    public PautaService(PautaRepository repository) {
        this.repository = repository;
    }

    @Transactional
    public Pauta criar(String titulo, String descricao) {
        var pauta = repository.save(new Pauta(titulo, descricao));
        log.info("Pauta criada: id={}", pauta.getId());
        return pauta;
    }

    @Transactional(readOnly = true)
    public Pauta buscar(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new PautaNaoEncontradaException(id));
    }

    @Transactional(readOnly = true)
    public Page<Pauta> listar(Pageable pageable) {
        return repository.findAll(pageable);
    }
}