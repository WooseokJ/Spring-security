package com.cos.jwt.jwt;

public interface JwtProperties {
    String SECRET = "아무개나 cos같은"; // 우리 서버만 알고 있는 비밀값
    int EXPIRATION_TIME = 600000; // 10분 (1000 이 1초 , 60 * 1000 * 10 = 1분 * 10)
    String TOKEN_PREFIX = "Bearer ";
    String HEADER_STRING = "Authorization";
}
