package net.dima.project.service;

import java.util.Optional;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.dima.project.dto.UserDTO;
import net.dima.project.entity.UserEntity;
import net.dima.project.repository.UserRepository;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository repository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    /**
     * 회원 가입 처리
     * @param userDTO 사용자 DTO
     * @return 성공 여부 (조건 불충족 시 false)
     */
    public boolean joinProc(UserDTO userDTO) {
        String userId = userDTO.getUserId();
        String email = userDTO.getEmail();

        // 아이디 길이 검사
        if (userId.length() < 3 || userId.length() > 5) {
            log.warn(" 아이디 길이 유효성 실패: {}", userId);
            return false;
        }

        // 이메일 형식 검사 (정규식 사용)
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            log.warn(" 이메일 형식 유효성 실패: {}", email);
            return false;
        }

        // 아이디 중복 검사
        if (repository.existsByUserId(userId)) {
            log.warn(" 중복 아이디: {}", userId);
            return false;
        }

        // 비밀번호 암호화
        String encodedPwd = bCryptPasswordEncoder.encode(userDTO.getUserPwd());
        userDTO.setUserPwd(encodedPwd);

        // DTO → Entity 변환
        UserEntity userEntity = UserEntity.toEntity(userDTO);

        // 저장
        repository.save(userEntity);
        log.info(" 회원가입 성공: {}", userId);
        return true;
    }

    /**
     * 아이디 중복 확인용 메서드 (Ajax 요청 대응)
     * @param userId
     * @return 존재하면 true, 없으면 false
     */
    public boolean existsByUserId(String userId) {
        return repository.existsByUserId(userId);
    }

	public UserDTO selectOne(String userId) {
		Optional<UserEntity> temp = repository.findById(userId);
	      UserDTO dto = null;
	      
	      if(temp.isPresent()) {
	         dto = UserDTO.toDTO(temp.get());
	      }
	      
	      log.info("아이디 검색: {}", dto);
	      return dto;
	}

	public String findRealNameById(String userId) {
		  return repository.findById(userId)
	                .map(user -> user.getUserName())
	                .orElse("알수없음");
	}
}
