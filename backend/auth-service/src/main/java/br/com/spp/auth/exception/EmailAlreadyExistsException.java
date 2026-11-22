package br.com.spp.auth.exception;

/**
 * Lançada quando se tenta registrar um e-mail já existente.
 * A mensagem é neutra para não permitir enumeração de usuários.
 */
public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {
        super("Não foi possível concluir o registro com os dados informados");
    }
}
