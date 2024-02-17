package com.cos.security.repository;

import com.cos.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

// JPARepository가 crud함수 구현되어있어
// @Repository 없어도 Ioc((스프링 컨테이너 등록)가 됨. 이유: JPARepository를 상속했기때문에 UserRepository가 Bean으로 등록됨.
public interface UserRepository extends JpaRepository<User, Long> {

    // findBy 규칙(참고: jpa query method로 검색.)
    // select * from user where username = ?
    public User findByUsername(String username);


}
