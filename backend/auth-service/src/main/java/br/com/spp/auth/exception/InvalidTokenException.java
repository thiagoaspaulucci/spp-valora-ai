package br.com.spp.auth.exception;

/**
 * Lançada quando um refresh token é inválido, expirado ou de tipo incorreto.
 */
public class InvalidTokenException extends RuntimeException {

    public InvalidTokenException(String message) {
        super(message);
    }
}
