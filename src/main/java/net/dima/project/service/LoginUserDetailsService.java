package net.dima.project.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima.project.dto.LoginUserDetails;
import net.dima.project.entity.UserEntity;
import net.dima.project.repository.UserRepository;

// Security가 제공하는 로그인 전용 Service 클래스
@Service
@RequiredArgsConstructor
@Slf4j
public class LoginUserDetailsService implements UserDetailsService {

    private final UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String userId)
            throws UsernameNotFoundException {

        // userId 기준으로 사용자 조회
        UserEntity temp = repository.findById(userId)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 아이디입니다."));

        System.out.println(temp.toString());

        LoginUserDetails userDTO = LoginUserDetails.toDTO(temp);
        return userDTO;
    }
}
