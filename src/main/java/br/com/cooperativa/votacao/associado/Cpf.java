package br.com.cooperativa.votacao.associado;

public final class Cpf {

    private Cpf() {
    }

    /** Mascara o CPF para logs, mantendo só os 2 últimos dígitos. */
    public static String mascarar(String cpf) {
        if (cpf == null || cpf.length() < 4) {
            return "***";
        }
        return "*********" + cpf.substring(cpf.length() - 2);
    }
}