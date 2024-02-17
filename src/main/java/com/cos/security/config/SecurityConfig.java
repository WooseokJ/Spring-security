package com.cos.security.config;

import com.cos.security.oauth.PrincipalOauth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity // 시큐리티 활성화 시키기 위함. -> 스프링 시큐리티 필터(SecurityConfig) 가 스프링 필터체인에 등록됨.
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true) // secured(최신꺼), (예전꺼) preAuthorize(postAuthorize도같이) 어노테이션 활성화
@RequiredArgsConstructor
public class SecurityConfig {


    private final PrincipalOauth2UserService principalOauth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        HttpSecurity httpSecurity = http.csrf(csrf -> csrf.disable())
                .authorizeRequests(auth -> {
                    // /user, /manage, /admin 주소이외는 누구나 들어갈수있다.
                        auth.requestMatchers("/user/**").authenticated() // 인증만 되면 들어갈수있는 주소.
                            .requestMatchers("/manager/**").access("hasRole('ROLD_ADMIN') or hasRole('ROLE_MANAGER')")
                            .requestMatchers("/admin/**").access("hasRole('ROLD_ADMIN')")
                            .anyRequest().permitAll(); // 나머지주소는 권한 허용되있음.
                }).httpBasic(withDefaults());

        // /admin, /user, /manage로 url검색해도  /loginForm 페이지로 이동한다.
        httpSecurity
                .formLogin(form -> form
                        .loginPage("/loginForm") // url : /loginForm
                        .loginProcessingUrl("/login") // /login 주소가 호출시 -> 시큐리티가 낚아채서 대신 로그인 진행.( controller에 /login 안만들어도됨)
                        .defaultSuccessUrl("/") // /loginForm url로 들어와서 로그인 성공하면 메인페이로 이동 ,
                                                // 그런데 /user,admin같은 거로 /loginForm으로 오면 로그인성공하면 /user, admin 페이지로 보내줌.
                        .permitAll()
        );

        /*
        소셜로그인 원래 과정
         step1. code 받기(인증완료, 로그인이된 정상사용자),
         step2. 엑세스토큰(코드를통해 권한이생겨)
         step3. 사용자 프로필정보 가져옴. ex) 이름,아이디, 이메일, 전번
         step4. 그 정보 토대로 회원가입 자동 진행시킴.
         만약 정보가 부족하면 ex) 집주소, 회원등급 이면 회원가입페이지로 이동시켜야해

        * */
        // oauth 로그인


        httpSecurity
                .oauth2Login(form -> form
                        .loginPage("/loginForm") // 여기까지만하면 엑세스차단뜨면서 403 된다.
                        // 구글로그인이 완료된뒤 후처리가 필요함 (그런데 Oatuh라이브러리가 코드받는게아닌 엑세스토큰+사용자프로필정보 한번에 받는다.)
                        .userInfoEndpoint(userinfo -> userinfo // 엑세스토큰+사용자프로필정보
                                .userService(principalOauth2UserService)
                        )
                );




        return httpSecurity.build();

    }

    // pw 암호화
    @Bean // 해당 메서드의 리턴되는 객체를 Ioc(스프링 컨테이너)로 등록해줌.
    public BCryptPasswordEncoder encodePwd() {
        return new BCryptPasswordEncoder();
    }
}


