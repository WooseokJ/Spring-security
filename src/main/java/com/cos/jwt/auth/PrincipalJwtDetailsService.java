package com.cos.jwt.auth;

import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserJwtRepository;
import com.cos.security.auth.PrincipalDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 8080/login 요청올떄 동작. (스프링 시큐리티 기본적으로 로그인요청주소가 /login이다.)
// 하지만 securityConfig에서 .formLogin을 disable해서 동작안함.
@Service
@RequiredArgsConstructor
public class PrincipalJwtDetailsService implements UserDetailsService {
    private final UserJwtRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("이름 = " + username);
        User findUser = userRepository.findByUsername(username);
        System.out.println("findUser.pw = " + findUser.getPassword());
        return new PrincipalJwtDetails(findUser);

    }
}
