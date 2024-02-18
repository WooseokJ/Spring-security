package com.cos.jwt.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.auth.PrincipalJwtDetails;
import com.cos.jwt.model.User;
import com.cos.jwt.repository.UserJwtRepository;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

// 시큐리티가 갖고있는 필터중 BasicAuthenticationFilter가 있는데
// 권한,인증이 필요한 특정 주소 요청시 위 필터를 무조건 거친다
// 만약 인증 필요없는 주소이면 위 필터 안거친다.

public class JwtAuthorizationFilter extends BasicAuthenticationFilter {
    private final UserJwtRepository userJwtRepository;

    public JwtAuthorizationFilter(AuthenticationManager authenticationManager, UserJwtRepository userJwtRepository) {
        super(authenticationManager);
        this.userJwtRepository = userJwtRepository;
    }



    // 인증권한 요청 필요한 주소요청이 해당 필터 거친다.
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("인증이나 권한이 필요한 주소 요청이됨.");
        String jwtHeader = request.getHeader(JwtProperties.HEADER_STRING); // Authorization
        System.out.println("jwtHeader = " + jwtHeader);

        // header 있는지 확인.
        if(jwtHeader == null || !jwtHeader.startsWith(JwtProperties.TOKEN_PREFIX)) { // null이거나 시작이 Bearer 아니면
            chain.doFilter(request,response);
            return;
        }

        // jwt 토큰을 검증해서 정상적인 사용자인지 확인
        String jwtToken = jwtHeader.replace(JwtProperties.TOKEN_PREFIX, ""); // Bearer을 공백으로 치환
        // 서명이 정상이면 username 가져옴.
        String username = JWT.require(Algorithm.HMAC512("cos")).build().verify(jwtToken).getClaim("username").asString(); // string으로 케스팅
        if(username !=null) { // null아니면 서명이 정상이된거.
            User findUser = userJwtRepository.findByUsername(username);

            PrincipalJwtDetails principalJwtDetails = new PrincipalJwtDetails(findUser);
            // jwt토큰 서명을통해 서명이 정상이면 Authentication 객체 만들어줌.
            Authentication authentication = new UsernamePasswordAuthenticationToken(principalJwtDetails, null, principalJwtDetails.getAuthorities()); // null은 pw 이다
            // 강제로 시큐리티 세션에 접근해 Authentication 객체를 저장.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        chain.doFilter(request,response);
    }
}
