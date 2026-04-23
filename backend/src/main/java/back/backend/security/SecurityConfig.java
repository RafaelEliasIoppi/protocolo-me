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

import org.springframework.web.cors.*;

import org.springframework.beans.factory.annotation.Value;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

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
            .cors().configurationSource(corsConfigurationSource()).and()
            .csrf().disable()

            // =========================
            // EXCEPTION HANDLING
            // =========================
            .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler())
                .accessDeniedHandler(accessDeniedHandler()).and()

            // =========================
            // SESSÃO (JWT)
            // =========================
            .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()

            // =========================
            // AUTORIZAÇÃO
            // =========================
            .authorizeRequests()
                // ---------- PUBLIC ----------
                .antMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                .antMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .antMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/usuarios/admin/registrar").permitAll()
                .antMatchers("/h2-console/**").permitAll()

                // ---------- USUÁRIOS ----------
                .antMatchers(HttpMethod.PATCH, "/api/usuarios/minha-senha").authenticated()
                .antMatchers("/api/usuarios/**").hasRole("ADMIN")

                // ---------- PACIENTES ----------
                .antMatchers(HttpMethod.GET, "/api/pacientes/**")
                    .hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")
                .antMatchers(HttpMethod.POST, "/api/pacientes/**")
                    .hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")
                .antMatchers(HttpMethod.PUT, "/api/pacientes/**")
                    .hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")
                .antMatchers(HttpMethod.PATCH, "/api/pacientes/**")
                    .hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")
                .antMatchers(HttpMethod.DELETE, "/api/pacientes/**")
                    .hasAnyRole("ADMIN","MEDICO")

                // ---------- HOSPITAIS ----------
                .antMatchers("/api/hospitais/**")
                    .hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")

                // ---------- CENTRAIS ----------
                .antMatchers(HttpMethod.GET, "/api/centrais-transplantes/**")
                    .hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")
                .antMatchers("/api/centrais-transplantes/**")
                    .hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN")

                // ---------- PROTOCOLOS ----------
                .antMatchers(HttpMethod.GET, "/api/protocolos-me/**")
                    .hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")
                .antMatchers("/api/protocolos-me/**")
                    .hasAnyRole("MEDICO","ENFERMEIRO","ADMIN","CENTRAL_TRANSPLANTES")

                // ---------- EXAMES ----------
                .antMatchers(HttpMethod.GET, "/api/exames-me/**")
                    .hasAnyRole("ADMIN","MEDICO","ENFERMEIRO","COORDENADOR_TRANSPLANTES","CENTRAL_TRANSPLANTES")
                .antMatchers("/api/exames-me/**")
                    .hasAnyRole("MEDICO","ENFERMEIRO","ADMIN")

                // ---------- ESTATÍSTICAS ----------
                .antMatchers(HttpMethod.GET,
                        "/api/centrais-transplantes/estatisticas/doadores-receptores")
                    .hasRole("CENTRAL_TRANSPLANTES")
                .antMatchers("/api/estatisticas-transplantes/**")
                    .hasAnyRole("CENTRAL_TRANSPLANTES","ADMIN","MEDICO")

                // ---------- DEFAULT ----------
                .anyRequest().authenticated().and()

            // =========================
            // HEADERS (H2)
            // =========================
            .headers().frameOptions().sameOrigin().and()

            // =========================
            // JWT FILTER
            // =========================
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // =====================================================
    // CORS CONFIG
   @Value("${app.cors.allowed-origins:https://*.app.github.dev}")
    private String allowedOrigins;
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of(allowedOrigins.split(",")));
        config.setAllowedMethods(List.of("GET","POST","PUT","DELETE","PATCH","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setExposedHeaders(List.of("Authorization"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);

        return source;
    }

    // =====================================================
    // HANDLERS
    // =====================================================
    @Bean
    public AuthenticationEntryPoint unauthorizedHandler() {
        return (request, response, authException) -> {
            response.setStatus(401);
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\":\"Não autenticado\"}");
        };
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(403);
            response.setContentType("application/json");
            response.getWriter().write("{\"erro\":\"Acesso negado\"}");
        };
    }
}
