package back.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

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
            .csrf().disable()
            .authorizeRequests()
                .antMatchers(HttpMethod.POST, "/api/usuarios").permitAll()
                .antMatchers(HttpMethod.POST, "/api/usuarios/login").permitAll()
                .antMatchers("/h2-console/**").permitAll()
                .antMatchers("/api/usuarios/**").hasRole("ADMIN")
                .antMatchers("/api/pacientes/**").hasAnyRole("ADMIN", "MEDICO", "ENFERMEIRO")
                .anyRequest().authenticated()
            .and()
                .headers().frameOptions().sameOrigin()
            .and()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);
    }
}
