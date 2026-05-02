package net.dima.project.repository;

import net.dima.project.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<UserEntity, String> {

    // 아이디 중복 체크
    boolean existsByUserId(String userId);

    // 작성자 이름(실명)으로 매칭되는 유저들의 아이디 목록 반환
    @Query("select u.userId from UserEntity u where u.userName like %:name%")
    List<String> findUserIdsByUserNameLike(@Param("name") String name);

  
}
