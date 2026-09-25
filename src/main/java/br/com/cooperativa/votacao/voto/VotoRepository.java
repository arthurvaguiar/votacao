package br.com.cooperativa.votacao.voto;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface VotoRepository extends JpaRepository<Voto, Long> {

    boolean existsByPautaIdAndAssociadoId(Long pautaId, String associadoId);

    @Query("""
        select new br.com.cooperativa.votacao.voto.ContagemVoto(v.opcao, count(v))
        from Voto v
        where v.pautaId = :pautaId
        group by v.opcao
        """)
    List<ContagemVoto> contarPorOpcao(@Param("pautaId") Long pautaId);
}