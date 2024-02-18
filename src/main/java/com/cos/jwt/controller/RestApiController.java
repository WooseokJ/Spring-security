package com.cos.jwt.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

//@CrossOrigin // 시큐리티 인증필요한 요청은 다 거부
@RestController
public class RestApiController {

    @GetMapping("/home")
    public String home() {
        return "<h1>home</h1>";
    }


    @PostMapping("/token")
    public String token() {
        return "<h1>Totken</h1>";
    }
}
