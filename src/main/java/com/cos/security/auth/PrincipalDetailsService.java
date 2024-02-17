package com.cos.security.auth;

import com.cos.security.model.User;
import com.cos.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// 시큐리티 설정에서 loginProcessingUrl('/login') 해두어서
// /login 요청이 오면 자동으로  loadUserByUsername 호출.

@Service // ioC(스프링 컨테이너) 에 등록됨.
@RequiredArgsConstructor
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // username: html에 있는 <input name= username >의 값이 들어옴.
        // 만약 username 대신 바꾸고싶다면 SecurityConfig에서  .usernameParameter("username2") 같이 바꿀수있다.(가능하면 안바꾸는게좋다)
        System.out.println("username = " + username);

        User findUser = userRepository.findByUsername(username);
        if(findUser != null) { // 찾았으면
            return new PrincipalDetails(findUser); // Authentication객체 내부에 들어간뒤 -> Authentication객체가 시큐리티 세션에 넣어짐.
        }

        return null;
    }
}
