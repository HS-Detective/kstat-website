package net.dima.project.entity;

import jakarta.persistence.*;
import lombok.*;
import net.dima.project.dto.UserDTO;

@Entity
@Table(name = "kitauser")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserEntity {

    @Id
    @Column(name = "user_id", nullable = false, length = 50)
    private String userId;

    @Column(name = "user_name", nullable = false, length = 50)
    private String userName;

    @Column(name = "user_pwd", nullable = false, length = 100)
    private String userPwd;

    @Column(name = "email", nullable = false, unique = true, length = 50)
    private String email;

    @Builder.Default
    private String roles = "ROLE_USER"; // 기본값 지정

    @Builder.Default
    private boolean enabled = true;

    public static UserEntity toEntity(UserDTO userDTO) {
        return UserEntity.builder()
                .userId(userDTO.getUserId())
                .userName(userDTO.getUserName())
                .userPwd(userDTO.getUserPwd())
                .email(userDTO.getEmail())
                .build();
    }
}
