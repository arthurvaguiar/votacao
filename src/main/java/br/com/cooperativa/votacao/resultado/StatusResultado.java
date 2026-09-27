package br.com.cooperativa.votacao.resultado;

public enum StatusResultado {
    EM_ANDAMENTO("Em andamento"),
    APROVADA("Aprovada"),
    REPROVADA("Reprovada"),
    EMPATE("Empate");

    private final String descricao;

    StatusResultado(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }

    public static StatusResultado apurar(boolean sessaoAberta, long votosSim, long votosNao) {
        if (sessaoAberta) {
            return EM_ANDAMENTO;
        }
        if (votosSim > votosNao) {
            return APROVADA;
        }
        if (votosNao > votosSim) {
            return REPROVADA;
        }
        return EMPATE;
    }
}