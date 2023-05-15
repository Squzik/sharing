package org.example.sharing.security.config;

import org.example.sharing.security.service.filter.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Collections;
import java.util.List;

@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final JwtFilter jwtFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .cors().configurationSource(corsConfigurationSource())
                .and()
                .httpBasic().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeRequests().antMatchers(
                "/swagger-resources",
                "/swagger-resources/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/api/v1/auth/login",
                "/api/v1/auth/registration",
                "/api/v1/auth/token",
                "/api/v1/auth/refresh",
                "/api/v1/auth/mail/send",
                "/api/v1/auth/mail/confirm",
                "/api/v1/auth/recovery",
                "/api/v1/auth/recovery/confirm",
                "/api/v1/auth/{userId}/recovery",
                "/v2/api-docs",
                "/webjars/**",
                "/v3/api-docs/**",
                "/api/v1/user/{\\d+}",
                "/api/v1/flat/filter",
                "/api/v1/flat/{\\d+}",
                "/api/v1/booking/flat/{\\d+}/dates",
                "/api/v1/photo",
                "/api/v1/reviews/user/{\\d+}",
                "/api/v1/reviews/flat/{\\d+}",
                "/api/v1/auth/{\\d+}/password").permitAll()
                .anyRequest().authenticated()
                .and()
                .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .logout().logoutUrl("/api/auth/logout")
                .deleteCookies("JSESSIONID")
                .logoutSuccessUrl("/api/auth/login")
                .invalidateHttpSession(true)
                .and()
                .csrf().disable();
    }

    @Bean
    protected CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedHeaders(Collections.singletonList("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedMethods(Collections.singletonList("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

}
