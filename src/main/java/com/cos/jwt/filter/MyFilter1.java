package com.cos.jwt.filter;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;

public class MyFilter1 implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        System.out.println("필터1");

        // 임시토큰 만들어 테스트하려고 다운케스팅.
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;


        // 토큰이 오면 doFilter를 통해 controller 진입시키고 그게아니면 controller 진입안시킨다.
        // hihi 같은 토큰을 만들어야하는데 : id,pw 정상이고 로그인 완료되면 이떄 토큰 만들어주고 그걸 응답에 넣어 보내줌.
        // 요청때마다 Header에 Authorization에 value값으로 토큰을 가지고 오고
        // 그떄 토큰이 넘어오면 이 토큰이 내가만든 토큰이 맞는지 검증.(RSA, HS256으로 토큰검증하면됨)

        if(req.getMethod().equals("POST")) { // 요청온 http 메세지
            System.out.println("POST요청됨");
            String headerAuth = req.getHeader("Authorization");
            System.out.println("headerAuth = " + headerAuth);

            if(headerAuth.equals("hihi")) {
                chain.doFilter(req, res);
            } else {
                res.getWriter().println("인증안됨.");

            }
        }
    }
}
