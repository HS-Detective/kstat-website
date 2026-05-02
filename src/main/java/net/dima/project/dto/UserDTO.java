package net.dima.project.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.dima.project.entity.UserEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private String userId;
    private String userName;
    private String userPwd;
    private String pwdConfirm;
    private String email;
    private String roles;
    private boolean enabled;  

    public static UserDTO toDTO(UserEntity userEntity) {
        return UserDTO.builder()
                .userId(userEntity.getUserId())
                .userName(userEntity.getUserName())
                .userPwd(userEntity.getUserPwd())
                .email(userEntity.getEmail())
                .roles(userEntity.getRoles())
                .enabled(userEntity.isEnabled())  
                .build();
    }
}

