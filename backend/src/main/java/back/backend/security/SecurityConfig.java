package back.backend.security;

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
            .cors().and() // habilita CORS
            .csrf().disable()
            .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .antMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .antMatchers(HttpMethod.POST, "/api/usuarios/admin/registrar").permitAll()
                    .antMatchers(HttpMethod.POST, "/api/pacientes/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN", "CENTRAL_TRANSPLANTES")
                    .antMatchers(HttpMethod.PUT, "/api/pacientes/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN", "CENTRAL_TRANSPLANTES")
                    .antMatchers(HttpMethod.PATCH, "/api/pacientes/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN", "CENTRAL_TRANSPLANTES")
                .antMatchers(HttpMethod.DELETE, "/api/pacientes/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")

                .antMatchers(HttpMethod.POST, "/api/hospitais/**").hasAnyRole("CENTRAL_TRANSPLANTES", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/hospitais/**").hasAnyRole("CENTRAL_TRANSPLANTES", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/hospitais/**").hasAnyRole("CENTRAL_TRANSPLANTES", "ADMIN")

                .antMatchers(HttpMethod.POST, "/api/centrais-transplantes/**").hasAnyRole("CENTRAL_TRANSPLANTES", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/centrais-transplantes/**").hasAnyRole("CENTRAL_TRANSPLANTES", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/centrais-transplantes/**").hasAnyRole("CENTRAL_TRANSPLANTES", "ADMIN")

                .antMatchers(HttpMethod.POST, "/api/protocolos-me/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/protocolos-me/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/protocolos-me/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/protocolos-me/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/protocolos-me/**").hasAnyRole("ADMIN", "MEDICO", "ENFERMEIRO", "COORDENADOR_TRANSPLANTES", "CENTRAL_TRANSPLANTES")

                .antMatchers(HttpMethod.POST, "/api/exames-me/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/exames-me/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/exames-me/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/exames-me/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")
                .antMatchers(HttpMethod.GET, "/api/exames-me/**").hasAnyRole("ADMIN", "MEDICO", "ENFERMEIRO", "COORDENADOR_TRANSPLANTES", "CENTRAL_TRANSPLANTES")

                .antMatchers(HttpMethod.GET, "/api/centrais-transplantes/estatisticas/doadores-receptores").hasRole("CENTRAL_TRANSPLANTES")

                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/api/usuarios/**").hasRole("ADMIN")
                .antMatchers("/api/pacientes/**").hasAnyRole("ADMIN", "MEDICO", "ENFERMEIRO", "COORDENADOR_TRANSPLANTES", "CENTRAL_TRANSPLANTES")
                .antMatchers("/api/hospitais/**").hasAnyRole("ADMIN", "MEDICO", "ENFERMEIRO", "COORDENADOR_TRANSPLANTES", "CENTRAL_TRANSPLANTES")
                .antMatchers("/api/centrais-transplantes/**").hasAnyRole("ADMIN", "CENTRAL_TRANSPLANTES", "COORDENADOR_TRANSPLANTES")
                .anyRequest().authenticated()
            .and()
                .headers().frameOptions().sameOrigin()
            .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Para desenvolvimento - permite qualquer origem
        // Em produção, isso deve ser restringido a domínios específicos
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        // Com allowedOriginPatterns = ["*"], allowCredentials deve ser false
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}