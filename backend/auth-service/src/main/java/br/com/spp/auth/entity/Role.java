package br.com.spp.auth.entity;

/**
 * Papéis (authorities) atribuíveis a um {@link User}.
 * O prefixo {@code ROLE_} segue a convenção do Spring Security.
 */
public enum Role {
    ROLE_USER,
    ROLE_ADMIN
}
