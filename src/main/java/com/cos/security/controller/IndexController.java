package com.cos.security.controller;

import com.cos.security.auth.PrincipalDetails;
import com.cos.security.model.User;
import com.cos.security.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.security.Principal;

@Controller
@RequiredArgsConstructor
public class IndexController {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;



    @GetMapping({"","/"})
    public String index() {
        // 기본적인 스프링이 권장하는것.(기본설정: src/main/resources/)
        // 뷰리졸버 설정: templates (prefix), mustache (suffix)
        return "index"; // index.html 리턴.(mystache에서 지원해주는 index.html 지원),
                        // /src/main/resource/templates/index.mustache로 잡혀있다. -> 변경하기위해 WebMvcConfig.java 만듬.

    }
    @ResponseBody
    @GetMapping("/user")
    public String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println("principalDetails = " + principalDetails.getUser());
        return "user";
    }

    @ResponseBody
    @GetMapping("/admin")
    public String admin() {
        return "admin";
    }

    @ResponseBody
    @GetMapping("/manager")
    public String manager() {
        return "manager";
    }

    // 스프링 시큐리티가 해당주소를 우선권 갖는다.
    @GetMapping("/loginForm")
    public String login() {
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm() {
        return "joinForm";
    }


    @ResponseBody
    @PostMapping("/join")
    public String join(User user) {
        user.setRole("ROLE_USER");

        // 암호화
        String rawPw = user.getPassword();
        String encPw = bCryptPasswordEncoder.encode(rawPw);
        user.setPassword(encPw);


        // 만약 위의 암호화과정없어도  비번 1234같은걸로 회원가입잘됨. (시큐리티로 로그인을 할수없음. 이유는 패스워드가 암호화가 안되서)
        // 그래서 위의 암호화과정 필요.
        userRepository.save(user);
        return "join";
    }

    @ResponseBody
    @PostMapping("/loginProc")
    public String loginProc() {
        return "loginProc";
    }

    @Secured("ROLE_ADMIN") // 특정 권한있는사람만 /info 주소 들어감.
    @ResponseBody
    @GetMapping("/info")
    public String info() {
        return "ㄱㅐ인정보";
    }
//    @PostAuthorize() // 메서드 실행이후
    @PreAuthorize("hasRole('ROLE_MANAGER') or hasRole('ROLE_ADMIN')") // 메서드 실행 직전
    @ResponseBody
    @GetMapping("/data")
    public String data() {
        return "데이터";
    }

    // 일반로그인 테스트(소셜로그인 접근불가)
    @ResponseBody
    @GetMapping("/test/login")
    public String loginTest(Authentication authentication,  // authentication에 DI됨.
                            @AuthenticationPrincipal PrincipalDetails userDetails) { // @AuthenticationPrincipal로 세션정보 접근가능.
        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal(); // 소셜로그인으로 하면 여기서 에러
        System.out.println("authentication = " + principalDetails.getUser());
        System.out.println("userDetails = " + userDetails.getUser()); // 위와 동일

        return "세션정보확인하기";


    }
    // 소셜로그인 테스트 (
    @ResponseBody
    @GetMapping("/test/oauth/login")
    public String loginOAuthTest(Authentication authentication,
                                 @AuthenticationPrincipal OAuth2User oauth) {
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        System.out.println("authentication = " + oAuth2User.getAttributes());
        System.out.println("oauth = " + oauth.getAttributes());  // 위와동일
        return "OAuth 세션정보확인하기";
    }

    //
    /* 정리: 스프링 시큐리티
     세션안에 시큐리티가 관리하는 세션 존재.(Authentication객체만 관리가능) 객체가 시큐리티세션에 들어가면 로그인이된거.-> controller에서 Authentication객체 DI에의해 가져올수있다.
    Authentication객체 안에 들어갈수있는 타입2개
    1. userDetails타입 -> 일반로그인
    2. OAuth2User타입 -> 소셜로그인

    */




}
