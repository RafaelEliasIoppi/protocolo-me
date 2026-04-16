package back.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().and()
            .csrf().disable()
            .authorizeRequests()
                // Registro público apenas para MEDICO e ENFERMEIRO
                .antMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .antMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()

                // Admin: criação de usuários privilegiados
                .antMatchers(HttpMethod.POST, "/api/usuarios/admin/registrar").hasRole("ADMIN")

                // Pacientes: mutação restrita a profissionais de saúde
                .antMatchers(HttpMethod.POST, "/api/pacientes/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/pacientes/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")
                .antMatchers(HttpMethod.PATCH, "/api/pacientes/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/pacientes/**").hasAnyRole("MEDICO", "ENFERMEIRO", "ADMIN")

                // Hospitais: mutação restrita à Central de Transplantes e Admin
                .antMatchers(HttpMethod.POST, "/api/hospitais/**").hasAnyRole("CENTRAL_TRANSPLANTES", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/hospitais/**").hasAnyRole("CENTRAL_TRANSPLANTES", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/hospitais/**").hasAnyRole("CENTRAL_TRANSPLANTES", "ADMIN")

                // Centrais de Transplantes: mutação restrita
                .antMatchers(HttpMethod.POST, "/api/centrais-transplantes/**").hasAnyRole("CENTRAL_TRANSPLANTES", "ADMIN")
                .antMatchers(HttpMethod.PUT, "/api/centrais-transplantes/**").hasAnyRole("CENTRAL_TRANSPLANTES", "ADMIN")
                .antMatchers(HttpMethod.DELETE, "/api/centrais-transplantes/**").hasAnyRole("CENTRAL_TRANSPLANTES", "ADMIN")

                // Protocolos ME: acesso a todos os profissionais autenticados
                .antMatchers("/api/protocolos-me/**").hasAnyRole("ADMIN", "MEDICO", "ENFERMEIRO", "COORDENADOR_TRANSPLANTES", "CENTRAL_TRANSPLANTES")

                // Exames ME: acesso a todos os profissionais autenticados
                .antMatchers("/api/exames-me/**").hasAnyRole("ADMIN", "MEDICO", "ENFERMEIRO", "COORDENADOR_TRANSPLANTES", "CENTRAL_TRANSPLANTES")

                // Console H2: apenas ADMIN (somente desenvolvimento)
                .antMatchers("/h2-console/**").hasRole("ADMIN")

                // Demais rotas de usuários: apenas ADMIN
                .antMatchers("/api/usuarios/**").hasRole("ADMIN")

                // Leitura de pacientes, hospitais e centrais: qualquer profissional autenticado
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
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Para desenvolvimento - permite qualquer origem
        // Em produção, deve ser restringido ao domínio do frontend
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setExposedHeaders(List.of("Authorization", "Content-Type", "X-Requested-With"));
        configuration.setAllowCredentials(false);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
