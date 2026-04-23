package back.backend.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final String corsAllowedOrigins;

    public SecurityConfig(JwtFilter jwtFilter,
                          @Value("${app.cors.allowed-origins}") String corsAllowedOrigins) {
        this.jwtFilter = jwtFilter;
        this.corsAllowedOrigins = corsAllowedOrigins;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .authorizeHttpRequests(authz -> authz
                // Endpoints públicos
                .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .requestMatchers(HttpMethod.POST, "/api/usuarios/admin/registrar").permitAll()
                .requestMatchers("/h2-console/**").permitAll()

                // Endpoints protegidos
                .requestMatchers(HttpMethod.PATCH, "/api/usuarios/minha-senha").authenticated()
                .requestMatchers("/api/usuarios/**").hasAnyRole("ADMIN", "COORDENADOR_TRANSPLANTES")

                // Pacientes
                .requestMatchers(HttpMethod.GET, "/api/pacientes/**").hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")
                .requestMatchers(HttpMethod.POST, "/api/pacientes/**").hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")
                .requestMatchers(HttpMethod.PUT, "/api/pacientes/**").hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")
                .requestMatchers(HttpMethod.PATCH, "/api/pacientes/**").hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")
                .requestMatchers(HttpMethod.DELETE, "/api/pacientes/**").hasAnyRole("MEDICO","ENFERMEIRO","ADMIN")

                // Hospitais
                .requestMatchers(HttpMethod.POST, "/api/hospitais/**").hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/hospitais/**").hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/hospitais/**").hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")

                // Centrais de transplantes
                .requestMatchers(HttpMethod.POST, "/api/centrais-transplantes/**").hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/centrais-transplantes/**").hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/centrais-transplantes/**").hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/centrais-transplantes/**").hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")

                // Protocolos ME
                .requestMatchers(HttpMethod.POST, "/api/protocolos-me/**").hasAnyRole("MEDICO","ENFERMEIRO","ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/protocolos-me/**").hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")
                .requestMatchers(HttpMethod.PATCH, "/api/protocolos-me/**").hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")
                .requestMatchers(HttpMethod.DELETE, "/api/protocolos-me/**").hasAnyRole("MEDICO","ENFERMEIRO","ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/protocolos-me/**").hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")

                // Exames ME
                .requestMatchers(HttpMethod.POST, "/api/exames-me/**").hasAnyRole("MEDICO","ENFERMEIRO","ADMIN")
                .requestMatchers(HttpMethod.PUT, "/api/exames-me/**").hasAnyRole("MEDICO","ENFERMEIRO","ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/exames-me/**").hasAnyRole("MEDICO","ENFERMEIRO","ADMIN")
                .requestMatchers(HttpMethod.DELETE, "/api/exames-me/**").hasAnyRole("MEDICO","ENFERMEIRO","ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/exames-me/**").hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")

                // Estatísticas
                .requestMatchers(HttpMethod.GET, "/api/centrais-transplantes/estatisticas/doadores-receptores").hasRole("CENTRAL_TRANSPLANTES")
                .requestMatchers(HttpMethod.PUT, "/api/estatisticas-transplantes/protocolo-me/**").hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")
                .requestMatchers(HttpMethod.POST, "/api/estatisticas-transplantes/protocolo-me/**").hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")
                .requestMatchers(HttpMethod.PATCH, "/api/estatisticas-transplantes/protocolo-me/**").hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")
                .requestMatchers(HttpMethod.GET, "/api/estatisticas-transplantes/protocolo-me/**").hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN","MEDICO")

                // Qualquer outra requisição precisa estar autenticada
                .anyRequest().authenticated()
            )
            .headers(headers -> headers.frameOptions().sameOrigin())
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        List<String> allowedOrigins = Arrays.stream(corsAllowedOrigins.split(","))
            .map(String::trim)
            .filter(s -> !s.isEmpty())
            .collect(Collectors.toList());

        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
