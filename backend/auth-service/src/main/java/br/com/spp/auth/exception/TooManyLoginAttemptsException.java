package br.com.spp.auth.exception;

/**
 * Lançada quando o número de tentativas de login falhas excede o limite.
 */
public class TooManyLoginAttemptsException extends RuntimeException {

    public TooManyLoginAttemptsException() {
        super("Muitas tentativas de login. Tente novamente mais tarde.");
    }
}
