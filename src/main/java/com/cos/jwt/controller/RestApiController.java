package com.cos.jwt.controller;

import com.cos.jwt.model.Roles;
import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserJwtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

//@CrossOrigin // 시큐리티 인증필요한 요청은 다 거부
@RestController
@RequiredArgsConstructor
public class RestApiController {
    private final UserJwtRepository userJwtRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    @GetMapping("/home")
    public String home() {
        return "<h1>home</h1>";
    }


    @PostMapping("/token")
    public String token() {
        return "<h1>Totken</h1>";
    }


    @PostMapping("/join")
    public String join(@RequestBody User user) {
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setRoles(Roles.USER);
        userJwtRepository.save(user);
        return "회원가입완료";
    }

    // user,manager, admin 권한만 접근가능
    @GetMapping("/api/v1/user")
    public String user() {
        return "user";
    }
    // manager, admin권한만 접근가능
    @GetMapping("/api/v1/manager")
    public String manager() {
        return "manager";
    }
    // admin권한만 접근가능.
    @GetMapping("/api/v1/admin")
    public String admin() {
        return "admin";
    }

}
