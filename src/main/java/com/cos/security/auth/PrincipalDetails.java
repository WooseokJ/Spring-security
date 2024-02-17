package com.cos.security.auth;


import com.cos.security.model.User;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

// 시큐리티가 /login 접속하면 낚아채서 로그인 진행시킨다.
// 로그인 완료시 시큐리티가 자신만의 session을 만들어줌.('security contextHolder'(key): 세션정보(value))
// 시큐리티의 session에 들어갈 객체 타입 -> Authentication 타입의 객체
// Authentication안에 User정보가 있어야함
// User 객체 타입 -> UserDefails 타입객체

// 시큐리티 Session(내부 Authentication(내부 UserDetails))
@Data
public class PrincipalDetails implements UserDetails, OAuth2User { // 상속받으면 UserDetails, PrincipalDetails 같은타입

    private User user;

    //일반로그인
    public PrincipalDetails(User user) {
        this.user = user;
    }

    // 해당 user의 권한을 리턴하는곳.
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // (user.getRole())string 타입 -> Collection으로 바꿈
        Collection<GrantedAuthority> collect = new ArrayList<>();
        collect.add(new GrantedAuthority() {
            @Override
            public String getAuthority() {
                return user.getRole();
            }
        });
        return collect;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 계정 만료되었니?
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    // 계정 잠겼니?
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    // 계정 비밀번호 기간지났니? , 너무오래사용했니?
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    // 계정 활성화되었니?
    @Override
    public boolean isEnabled() {
        // 우리 사이트에 1년동안 회원이 로그인 안하면? -> 휴먼계정으로 하기로 정함.
        // 현재시간 - 마지막 접속시간 >= 1년 초과 -> false
//        if (LocalDateTime.now().compareTo(user.getLastModifyDate()) > 0) { // 0대신 1년
//            return true;
//        }
        return true;
    }


    // OAuth2User
    private Map<String, Object> attributes;

    // OAuth로그인
    public PrincipalDetails(User user, Map<String, Object> attributes) {
        this.user = user; // attributes 토대로 User 객체 만들거야.
        this.attributes = attributes;
    }
    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }
    @Override
    public String getName() {// 안쓸꺼라 필요없음.
        return null;
    }

}





