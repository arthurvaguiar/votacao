package br.com.cooperativa.votacao.resultado;

public enum StatusResultado {
    EM_ANDAMENTO, APROVADA, REPROVADA, EMPATE;

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