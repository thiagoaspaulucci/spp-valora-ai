package br.com.spp.auth.mapper;

import br.com.spp.auth.dto.UserResponse;
import br.com.spp.auth.entity.Role;
import br.com.spp.auth.entity.User;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * Mapeamento entre a entidade {@link User} e seus DTOs de saída.
 * Ponto único de conversão (evita mapeamento espalhado pelos serviços).
 */
@Component
public class UserMapper {

    public UserResponse toResponse(User user) {
        Set<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());

        return new UserResponse(
                user.getId(),
                user.getName(),
                user.getEmail(),
                roles,
                user.isEnabled());
    }
}
