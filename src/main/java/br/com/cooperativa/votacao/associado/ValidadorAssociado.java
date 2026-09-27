package br.com.cooperativa.votacao.associado;

public interface ValidadorAssociado {

    /**
     * Garante que o associado pode votar; lança exceção caso contrário.
     */
    void validarPodeVotar(String cpf);
}