package net.dima.project.handler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoginSuccessHandler implements AuthenticationSuccessHandler {

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) throws IOException, ServletException {

        // 사용자 권한 로그 확인용
        List<String> roleNames = new ArrayList<>();
        authentication.getAuthorities().forEach(auth -> roleNames.add(auth.getAuthority()));
        log.info("로그인 사용자 권한: {}", roleNames);

        // 이전 페이지로 리다이렉트
        HttpSession session = request.getSession();
        String refererUrl = (String) session.getAttribute("refererUrl");
        log.info("리다이렉트 URL: {}", refererUrl);

        response.sendRedirect(refererUrl != null ? refererUrl : "/");
    }
}

