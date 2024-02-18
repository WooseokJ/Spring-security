package com.cos.jwt.jwt;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.cos.jwt.auth.PrincipalJwtDetails;
import com.cos.jwt.jwt.JwtProperties;
import com.cos.jwt.model.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.Date;

// 스프링 시큐리티에  UsernamePasswordAuthenticationFilter가 있음.
// /login 에 username, password post로 요청오면 동작.
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {
    private final AuthenticationManager authenticationManager;

    // login 요청을 하면 로그인시도위해 실행되는 함수
    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        try{
            // 1. username,pw 받아서
            // 이건 웹으로 요청올떄 input = username=user&password=1234 같이 온다.
//            BufferedReader br = request.getReader();
//            String input = null;
//            while ((input = br.readLine()) != null) {
//                System.out.println("input = " + input);
//            }

            // 안드,ios같은 json형태로 올떄
            ObjectMapper om = new ObjectMapper();
            User user = om.readValue(request.getInputStream(), User.class);
            System.out.println("user.name: " + user.getUsername());
            System.out.println("user.pw: " + user.getPassword());

            // 2.정상인지 로그인시도해보기 , AuthenticationManager로 로그인시도하면 PrincipalDetailsService가 loadUserByUsername() 함수 실행.
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
            // PrincipalDetailsService의 loadUserByUsername함수가 실행된후 정상이면 Authentication 반환.
            // Db에 있는 username,pw 가 일치.
            // 토큰넣으면 authenticationManager에서 인증을해줌
            Authentication authentication = authenticationManager.authenticate(authenticationToken);


            PrincipalJwtDetails principalJwtDetails =  (PrincipalJwtDetails) authentication.getPrincipal();
            System.out.println("로그인완료한 username: " + principalJwtDetails.getUsername()); // 값이나오면 로그인 정상되었다는것.
            // 3. principalJwtDetails를 세션이 담아(권한관리를위해서 하는것)
            // Authentication객체가 반환되면서 session영역에 저장 (시큐리티가 저장해줌.)
            //굳이 jwt 토큰사용하면서 세션만들이유없는데 권한처리떄문에 session 넣어줌.
            return authentication;

        }
        catch (IOException e) {
            e.printStackTrace();

        }





        return null;
    }

    // 위에 attemptAuthentication실행후 인증이 정상이면 아래함수 실행.

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException {
        // 4. jwt토큰 만들어 클라에게 jwt토큰을 반환해줌.
        System.out.println("인증성공");
        PrincipalJwtDetails principalJwtDetails = (PrincipalJwtDetails) authResult.getPrincipal();
        // RSA 방식이 아닌 Hash암호방식.
        // HMAC512: 시크릿값 있어야함  (이걸더 많이씀)
        // RSA: 공개키,개인키 갖고있어야해.
        String jwtToken = JWT.create()
                .withSubject("cos토큰")
                .withExpiresAt(new Date(System.currentTimeMillis() + JwtProperties.EXPIRATION_TIME)) // 토큰만료시간.
                .withClaim("id", principalJwtDetails.getUser().getId())
                .withClaim("username", principalJwtDetails.getUsername())
                .sign(Algorithm.HMAC512("cos"));
        response.addHeader("Authorization", "Bearer " + jwtToken);

    }
}
