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

    public static boolean valido(String cpf) {
        if (cpf == null || !cpf.matches("\\d{11}") || cpf.chars().distinct().count() == 1) {
            return false;
        }
        return digitoVerificador(cpf, 9) == cpf.charAt(9) - '0'
                && digitoVerificador(cpf, 10) == cpf.charAt(10) - '0';
    }

    private static int digitoVerificador(String cpf, int posicao) {
        int soma = 0;
        for (int i = 0; i < posicao; i++) {
            soma += (cpf.charAt(i) - '0') * (posicao + 1 - i);
        }
        int resto = (soma * 10) % 11;
        return resto == 10 ? 0 : resto;
    }
}