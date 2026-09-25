package br.com.cooperativa.votacao.voto;


import com.fasterxml.jackson.annotation.JsonCreator;

public enum OpcaoVoto {
    SIM, NAO;

    @JsonCreator
    public static OpcaoVoto de(String valor) {
        if (valor == null) {
            return null;
        }
        var normalizado = valor.trim().toUpperCase().replace("Ã", "A");
        return switch (normalizado) {
            case "SIM" -> SIM;
            case "NAO" -> NAO;
            default -> throw new IllegalArgumentException("Voto inválido: " + valor + ". Use 'Sim' ou 'Não'.");
        };
    }
}