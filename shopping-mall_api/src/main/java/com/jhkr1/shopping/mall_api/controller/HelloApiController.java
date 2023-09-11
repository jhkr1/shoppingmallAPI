package com.jhkr1.shopping.mall_api.controller;

import com.jhkr1.shopping.mall_api.security.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class HelloApiController {
    private final JwtTokenizer jwtTokenizer;

    @GetMapping("/hello")
    public String hello(@RequestHeader("Authorization") String token) {
        Long userIdFromToken = jwtTokenizer.getUserIdFromToken(token);
        return "hello " + userIdFromToken;
    }
}
