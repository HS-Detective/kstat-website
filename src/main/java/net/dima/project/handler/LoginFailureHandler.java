package net.dima.project.handler;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class LoginFailureHandler implements AuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(
            HttpServletRequest request,
            HttpServletResponse response,
            AuthenticationException exception) throws IOException, ServletException {

        String errMessage;

        if (exception instanceof BadCredentialsException) {
            errMessage = "아이디나 비밀번호가 잘못되었습니다.";
        } else {
            errMessage = "로그인에 실패했습니다.";
        }

        log.info("로그인 실패: {}", exception.getMessage());

        errMessage = URLEncoder.encode(errMessage, "UTF-8");
        response.sendRedirect("/user/login?error=true&errMessage=" + errMessage);
    }
}
