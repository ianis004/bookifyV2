package com.bookify.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

/**
 *Spring Security Configuration
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .authenticationProvider(authenticationProvider())
                .csrf(csrf -> csrf
                        .ignoringRequestMatchers(
                                new AntPathRequestMatcher("/h2-console/**"),
                                new AntPathRequestMatcher("/api/**")
                        )
                )
                .headers(headers -> headers
                        .frameOptions(frame -> frame.sameOrigin())
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(
                                new AntPathRequestMatcher("/"),
                                new AntPathRequestMatcher("/login"),
                                new AntPathRequestMatcher("/register"),
                                new AntPathRequestMatcher("/api/auth/**"),
                                new AntPathRequestMatcher("/h2-console/**"),
                                new AntPathRequestMatcher("/css/**"),
                                new AntPathRequestMatcher("/js/**"),
                                new AntPathRequestMatcher("/images/**"),
                                // ADD THESE HERE instead:
                                new AntPathRequestMatcher("/benchmark"),
                                new AntPathRequestMatcher("/api/data/**")
                        ).permitAll()

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/admin/**"),
                                new AntPathRequestMatcher("/admin/**")
                        ).hasAuthority("ADMIN")

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/staff/**"),
                                new AntPathRequestMatcher("/staff/**")
                        ).hasAnyAuthority("STAFF", "ADMIN")

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/appointments", "GET")
                        ).hasAnyAuthority("STAFF", "ADMIN")

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/appointments/status/**", "GET")
                        ).hasAnyAuthority("STAFF", "ADMIN")

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/appointments/my-appointments", "GET")
                        ).authenticated()

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/appointments", "POST")
                        ).authenticated()

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/appointments/**", "PUT"),
                                new AntPathRequestMatcher("/api/appointments/**", "DELETE")
                        ).hasAnyAuthority("STAFF", "ADMIN")

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/appointments/**", "GET")
                        ).authenticated()

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/services/**")
                        ).authenticated()

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/settings", "GET")
                        ).authenticated()

                        .requestMatchers(
                                new AntPathRequestMatcher("/api/settings/**")
                        ).hasAuthority("ADMIN")

                        .anyRequest().authenticated()  // this should be LAST
                )
                .formLogin(form -> form
                        .loginPage("/login")
                        .defaultSuccessUrl("/dashboard", true)
                        .permitAll()
                )
                .logout(logout -> logout
                        .logoutSuccessUrl("/login")
                        .permitAll()
                );

        return http.build();
    }
}