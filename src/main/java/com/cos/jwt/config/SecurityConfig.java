package com.cos.jwt.config;


import com.cos.jwt.jwt.JwtAuthenticationFilter;
import com.cos.jwt.filter.MyFilter3;
import com.cos.jwt.jwt.JwtAuthorizationFilter;
import com.cos.jwt.model.Roles;
import com.cos.jwt.repository.UserJwtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
    private final CorsFilter corsFilter;
    private final UserJwtRepository userJwtRepository;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        AuthenticationManager authenticationManager = http.getSharedObject(AuthenticationManagerBuilder.class).build();

        http.authenticationManager(authenticationManager);

        // SecurityFilterChain은 우선순위 순서들이 있다.(검색해서 참고하자 )
        http.addFilterBefore(new MyFilter3(), BasicAuthenticationFilter.class); // 스프링 필터로 바꿔줘야 등록이됨.

        HttpSecurity httpSecurity = http.csrf(csrf -> csrf.disable())
                .sessionManagement(o -> o.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // 새션으로 로그인방식 x = 무상태성으로 사용할거야
//                .addFilter(corsFilter) // 모든요청이 이 필터를 탄다(모든요청 허용으로해둠). 내서버는 cors(cross origin 정책)에서 벗어날수있다. @CrossOrigin(인증없을떄 사용, ) 시큐리티 필터에등록(인증있을떄 현재같이 사용)
                .formLogin(o -> o.disable()) // form만들어서 로그인은 안함.
                .httpBasic(o -> o.disable()) // 기본적인 http 로그인방식 x

                .addFilter(new JwtAuthenticationFilter(authenticationManager)) // AuthenticationManger 필요
                .addFilter(new JwtAuthorizationFilter(authenticationManager, userJwtRepository)) // AuthenticationManger 필요

                .authorizeRequests(auth ->
                        auth.requestMatchers("/api/v1/user/**")
                                .access("hasRole('ROLE_USER') " +
                                        "or hasRole('ROLE_ADMIN') " +
                                        "or hasRole('ROLE_MANAGER')")
                                .requestMatchers("/api/v1/manager/**")
                                .access("hasRole('ROLE_ADMIN') " +
                                        "or hasRole('ROLE_MANAGER')")
                                .requestMatchers("/api/v1/admin/**")
                                .access("hasRole('ROLE_ADMIN')")
                                .anyRequest().permitAll()
                )
                ;
        return httpSecurity.build();
    }


}
