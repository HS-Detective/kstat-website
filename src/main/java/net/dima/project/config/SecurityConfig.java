package net.dima.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.*;

import lombok.RequiredArgsConstructor;
import net.dima.project.handler.CustomLogoutSuccessHandler;
import net.dima.project.handler.LoginFailureHandler;
import net.dima.project.handler.LoginSuccessHandler;

import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final LoginSuccessHandler loginSuccessHandler;
    private final LoginFailureHandler loginFailureHandler;
    private final CustomLogoutSuccessHandler logoutSuccessHandler;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // CORS 활성화(아래 CorsConfigurationSource Bean 사용)
            .cors(cors -> cors.configurationSource(corsConfigurationSource()))
            // CSRF는 API 데모용으로 비활성화
            .csrf(csrf -> csrf.disable())
            // 권한 설정
            .authorizeHttpRequests(auth -> auth
                .requestMatchers(
                        "/", "/main",
                        "/user/**",
                        "/board/boardList", "/board/boardList/**",
                        "/board/boardDetail", "/boardDetail_user",
                        "/news/**",
                        "/chat/**", "/api/chat/**",
                        "/images/**", "/js/**", "/css/**", "/downloads/**",
                        "/error",
                        "/api/stats/**"
                ).permitAll()

                // 관리자 전용
                .requestMatchers("/board/boardDetail_manager", "/boardDetail_manager").hasRole("MANAGER")

                // 댓글 작성/수정/삭제 : MANAGER만
                .requestMatchers(HttpMethod.POST,   "/reply/**").hasRole("MANAGER")
                .requestMatchers(HttpMethod.PUT,    "/reply/**").hasRole("MANAGER")
                .requestMatchers(HttpMethod.DELETE, "/reply/**").hasRole("MANAGER")

                // 게시글 작성/수정/삭제 : USER 또는 MANAGER
                .requestMatchers(
                        "/board/boardWrite", "/board/boardWrite/**",
                        "/board/boardUpdate", "/board/boardUpdate/**",
                        "/board/boardDelete", "/board/boardDelete/**"
                ).hasAnyRole("USER", "MANAGER")

                // 프리플라이트(브라우저 OPTIONS 요청) 전부 허용
                .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()

                // 나머지는 인증 필요
                .anyRequest().authenticated()
            )
            // 로그인/로그아웃
            .formLogin(auth -> auth
                .loginPage("/user/login")
                .loginProcessingUrl("/user/loginProc")
                .usernameParameter("userId")
                .passwordParameter("userPwd")
                .successHandler(loginSuccessHandler)
                .failureHandler(loginFailureHandler)
                .permitAll()
            )
            .logout(auth -> auth
                .logoutUrl("/user/logout")
                .logoutSuccessHandler(logoutSuccessHandler)
                .invalidateHttpSession(true)
                .clearAuthentication(true)
            )
            // X-Frame-Options 및 CSP 설정
            .headers(headers -> headers
                .frameOptions(frameOptions -> frameOptions.sameOrigin())
                .contentSecurityPolicy(csp -> csp.policyDirectives("frame-ancestors 'self'"))
            );

        return http.build();
    }

    // CORS 설정: 로컬 프론트 도메인만 허용(필요한 포트만 남겨도 됨)
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();

        // 프론트(dev) 도메인들
        config.setAllowedOrigins(List.of(
            "http://localhost:5173", // Vite/React 등
            "http://localhost:3000", // CRA/Next 등
            "http://127.0.0.1:5173",
            "http://127.0.0.1:3000"
        ));

        // 필요한 메서드만
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // 프론트가 보내는 헤더들(필요 시 추가)
        config.setAllowedHeaders(List.of("Content-Type", "Accept"));

        // 인증 쿠키를 쓰지 않으니 false (세션/쿠키 쓰면 true)
        config.setAllowCredentials(false);

        // 캐시 타임(선택)
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // 모든 경로에 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    // 비밀번호 암호화용 Bean
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // 인증 관리자 설정
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
