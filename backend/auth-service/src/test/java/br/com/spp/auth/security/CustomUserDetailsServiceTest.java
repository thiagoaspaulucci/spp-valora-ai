package br.com.spp.auth.security;

import br.com.spp.auth.entity.Role;
import br.com.spp.auth.entity.User;
import br.com.spp.auth.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @Test
    void shouldLoadUserWithAuthorities() {
        User user = User.builder()
                .name("Maria")
                .email("maria@exemplo.com")
                .password("hash")
                .enabled(true)
                .roles(new HashSet<>(Set.of(Role.ROLE_USER)))
                .build();
        when(userRepository.findByEmail("maria@exemplo.com")).thenReturn(Optional.of(user));

        UserDetails details = new CustomUserDetailsService(userRepository)
                .loadUserByUsername("maria@exemplo.com");

        assertThat(details.getUsername()).isEqualTo("maria@exemplo.com");
        assertThat(details.getPassword()).isEqualTo("hash");
        assertThat(details.isEnabled()).isTrue();
        assertThat(details.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .contains("ROLE_USER");
    }

    @Test
    void shouldThrowWhenUserNotFound() {
        when(userRepository.findByEmail("nao@existe.com")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> new CustomUserDetailsService(userRepository)
                .loadUserByUsername("nao@existe.com"))
                .isInstanceOf(UsernameNotFoundException.class);
    }
}
