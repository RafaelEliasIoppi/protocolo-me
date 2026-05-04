package back.backend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.filter.CorsFilter;

import org.springframework.web.cors.*;

import org.springframework.beans.factory.annotation.Value;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            // =========================
            // CORS + CSRF
            // =========================
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            .csrf(csrf -> csrf.disable())

            // =========================
            // EXCEPTION HANDLING
            // =========================
            .exceptionHandling(exceptions -> exceptions
                .authenticationEntryPoint(unauthorizedHandler())
                .accessDeniedHandler(accessDeniedHandler())
            )

            // =========================
            // SESSÃO (JWT)
            // =========================
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            )

          .authorizeHttpRequests(auth -> auth

            // ---------- PUBLIC ----------
            .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
            .requestMatchers("/h2-console/**").permitAll()

            // ---------- USUÁRIOS ----------
            .requestMatchers(HttpMethod.PATCH, "/api/usuarios/minha-senha").authenticated()
            .requestMatchers("/api/usuarios/**").hasRole("ADMIN")

            // ---------- PACIENTES ----------
            .requestMatchers(HttpMethod.GET, "/api/pacientes/**")
                .hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")
            .requestMatchers(HttpMethod.POST, "/api/pacientes/**")
                .hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")
            .requestMatchers(HttpMethod.PUT, "/api/pacientes/**")
                .hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")
            .requestMatchers(HttpMethod.PATCH, "/api/pacientes/**")
                .hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")
            .requestMatchers(HttpMethod.DELETE, "/api/pacientes/**")
                .hasAnyRole("ADMIN","MEDICO")

            // ---------- HOSPITAIS ----------
            .requestMatchers(HttpMethod.GET, "/api/hospitais/**")
                .hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")
            .requestMatchers("/api/hospitais/**")
                .hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")

            // ---------- CENTRAIS ----------
            .requestMatchers(HttpMethod.GET, "/api/centrais-transplantes/**")
                .hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")
            .requestMatchers("/api/centrais-transplantes/**")
                .hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")

            // ---------- PROTOCOLOS ----------
            .requestMatchers(HttpMethod.GET, "/api/protocolos-me/**")
                .hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")
            .requestMatchers("/api/protocolos-me/**")
                .hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")

            // ---------- EXAMES ----------
            .requestMatchers(HttpMethod.GET, "/api/exames-me/**")
                .hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")
            .requestMatchers("/api/exames-me/**")
                .hasAnyRole("MEDICO","ENFERMEIRO","ADMIN")

            // ---------- ESTATÍSTICAS ----------
            .requestMatchers(HttpMethod.GET,
                    "/api/centrais-transplantes/estatisticas/doadores-receptores")
                .hasRole("CENTRAL_TRANSPLANTES")
            .requestMatchers("/api/estatisticas-transplantes/**")
                .hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN","MEDICO")

            // ---------- DEFAULT ----------
            .anyRequest().authenticated()
        )

            // =========================
            // HEADERS (H2)
            // =========================
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))

            // =========================
            // JWT FILTER
            // =========================
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // =====================================================
// CORS CONFIG
// =====================================================

@Value("${app.cors.allowed-origins:}")
private String allowedOrigins;

@Value("${app.cors.debug:false}")
private boolean corsDebug;

@Bean
public CorsConfigurationSource corsConfigurationSource() {
    CorsConfiguration config = new CorsConfiguration();

    List<String> origins = resolveAllowedOrigins();

    // 🔥 CORREÇÃO PRINCIPAL
    config.setAllowedOrigins(origins);

    // 🔥 garante funcionamento do preflight
    config.addAllowedMethod("*");
    config.addAllowedHeader("*");

    config.setAllowCredentials(true);
    config.setExposedHeaders(List.of("Authorization"));
    config.setMaxAge(3600L);

    if (corsDebug) {
        log.info("CORS origins ativos: {}", origins);
    }

    UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
    source.registerCorsConfiguration("/**", config);

    return source;
}

private List<String> resolveAllowedOrigins() {
    List<String> list = new ArrayList<>();

    if (allowedOrigins != null && !allowedOrigins.isBlank()) {
        Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(s -> !s.isBlank())
                .forEach(list::add);
    }

    return list;
}

    // =====================================================
    // HANDLERS
    // =====================================================
    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return (request, response, authException) -> {
            response.setStatus(401);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"erro\":\"Não autenticado\"}");
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(403);
            response.setCharacterEncoding("UTF-8");
            response.setContentType("application/json;charset=UTF-8");
            response.getWriter().write("{\"erro\":\"Acesso negado\"}");
        };
    }
}
