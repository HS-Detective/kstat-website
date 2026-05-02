package net.dima.project.dto;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import lombok.Getter;
import net.dima.project.entity.UserEntity;

@Getter
public class LoginUserDetails implements UserDetails {

    private final UserEntity user;

    public LoginUserDetails(UserEntity user) {
        this.user = user;
    }
    


    public static LoginUserDetails toDTO(UserEntity userEntity) {
        return new LoginUserDetails(userEntity);
    }
    
    public String getUserName() {
        return user.getUserName();
    }

    public String getRole() {
        return user.getRoles();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(user.getRoles()));
    }

    @Override
    public String getPassword() {
        return user.getUserPwd();
    }

    @Override
    public String getUsername() {
        return user.getUserId(); // 로그인 기준은 userId
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }
}
