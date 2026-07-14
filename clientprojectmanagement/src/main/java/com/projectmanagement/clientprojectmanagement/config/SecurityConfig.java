package com.projectmanagement.clientprojectmanagement.config;

import com.projectmanagement.clientprojectmanagement.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints
                        .requestMatchers("/users/login", "/users/register").permitAll()
                        // Static files
                        .requestMatchers(
                                "/", "/index.html", "/login.html",
                                "/Admin.html", "/MyTasks.html", "/Profile.html",
                                "/AllProjects.html", "/AdminProject.html",
                                "/AdminMessages.html", "/Clients.html",
                                "/CreateProject.html", "/TaskBoard.html",
                                "/MyProjects.html", "/deadlines.html",
                                "/Messages.html", "/ClientChat.html", "/Project.html",
                                "/auth.js", "/*.js", "/*.css", "/*.png",
                                "/*.jpg", "/*.ico", "/*.svg", "/*.woff2"
                        ).permitAll()
                        // API endpoints — all authenticated
                        .requestMatchers("/users/**").authenticated()
                        .requestMatchers("/projects/**").authenticated()
                        .requestMatchers("/clients/**").authenticated()
                        .requestMatchers("/messages/**").authenticated()
                        .requestMatchers("/general-messages/**").authenticated()
                        .requestMatchers("/tasks/**").authenticated()
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOriginPatterns(List.of("*"));
        config.setAllowedMethods(List.of("GET","POST","PUT","PATCH","DELETE","OPTIONS"));
        config.setAllowedHeaders(List.of("*"));
        config.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}