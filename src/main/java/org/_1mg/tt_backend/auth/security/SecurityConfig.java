package org._1mg.tt_backend.auth.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org._1mg.tt_backend.auth.entity.Role;
import org._1mg.tt_backend.auth.jwt.JwtFilter;
import org._1mg.tt_backend.auth.jwt.JwtUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;

import java.util.Collections;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtUtils jwtUtils;
    private final ObjectMapper objectMapper;
    private final CustomAuthenticationProvider authenticationProvider;

    private final CustomAuthenticationSuccessHandler successHandler;
    private final CustomAuthenticationFailureHandler failureHandler;
    private final CustomAuthenticationEntryPoint entryPoint;
    private final CustomAccessDeniedHandler accessDeniedHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {

        http
                .csrf(AbstractHttpConfigurer::disable);
        http
                .cors((corsCustomizer -> corsCustomizer.configurationSource(new CorsConfigurationSource() {

                    @Override
                    public CorsConfiguration getCorsConfiguration(HttpServletRequest request) {

                        CorsConfiguration configuration = new CorsConfiguration();

                        //브라우저 환경에서 WebSocket과 CORS 설정이 충돌됨 그래서 Patterns() 메소드를 써야 함?
                        //configuration.setAllowedOrigins(Collections.singletonList("*"));
                        configuration.setAllowedOriginPatterns(Collections.singletonList("*"));
                        configuration.setAllowedMethods(Collections.singletonList("*"));
                        configuration.setAllowCredentials(true);
                        configuration.setAllowedHeaders(Collections.singletonList("*"));
                        configuration.setMaxAge(3600L);

                        configuration.setExposedHeaders(Collections.singletonList("Authorization"));

                        return configuration;
                    }
                })));
        http
                .formLogin(AbstractHttpConfigurer::disable);
        http
                .logout(AbstractHttpConfigurer::disable);
        http
                .httpBasic(AbstractHttpConfigurer::disable);
        http
                .sessionManagement(session -> {
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                });
        http
                .authorizeHttpRequests((auth) -> {
                    auth
                            ///error 경로 permitAll()에 포함시키면 JWT 에러가 EntryPoint로 넘어가지 않음 왜지?
                            .requestMatchers("/", "/auth/**").permitAll()
                            .requestMatchers("/swagger/**", "/swagger-resources/**").permitAll()
                            .requestMatchers("/chat/**", "/pub/**", "/sub/**").hasRole(Role.ROLE_USER.getValue())
                            .requestMatchers("/test/**").permitAll()

                            .requestMatchers("/user").hasRole(Role.ROLE_USER.getValue())
                            .anyRequest().authenticated();
                });
        http
                .addFilterAt(customLoginFilter(authenticationManager), UsernamePasswordAuthenticationFilter.class);
        http
                .addFilterBefore(new JwtFilter(jwtUtils), UsernamePasswordAuthenticationFilter.class);

        http
                .exceptionHandling(exception ->
                        exception.authenticationEntryPoint(entryPoint)
                                .accessDeniedHandler(accessDeniedHandler)
                );

        return http.build();
    }

    @Bean
    public CustomLoginFilter customLoginFilter(AuthenticationManager authenticationManager) {

        CustomLoginFilter customLoginFilter = new CustomLoginFilter(authenticationManager, objectMapper);
        customLoginFilter.setFilterProcessesUrl("/auth/login");
        customLoginFilter.setAuthenticationSuccessHandler(successHandler);
        customLoginFilter.setAuthenticationFailureHandler(failureHandler);

        return customLoginFilter;
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        return http
                .getSharedObject(AuthenticationManagerBuilder.class)
                .authenticationProvider(authenticationProvider)
                //parent를 null로 해줘야 인증 실패 시 두 번 인증하지 않음
                .parentAuthenticationManager(null)
                .build();
    }
}