package com.jhkr1.shopping.mall_api.util;

import com.jhkr1.shopping.mall_api.security.jwt.util.JwtTokenizer;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;

import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;

@SpringBootTest
public class JwtTokenizerTest {
    @Autowired
    JwtTokenizer jwtTokenizer;

    @Value("${jwt.secretKey}")
    String accessSecret;
    // 60 * 30 * 1000 -> 30분
    public final Long ACCESS_TOKEN_EXPIRE_COUNT = 30 * 60 * 1000L;

    @Test // JWT 토큰을 생성
    public void createToken() throws Exception{
        String email = "wlgjs0606@gmail.com";
        List<String> roles = List.of("ROLE_USER"); // ["ROLE_USER"]
        Long id = 1L;
        // JWT 토큰의 payload에 들어갈 내용(claims)을 설정
        Claims claims = Jwts.claims().setSubject(email);
        // JWt 토큰의 payload에 들어갈 내용을 사용자가 추가하는 메서드는 put
        claims.put("roles", roles);
        claims.put("userId", id);

        // application.yml파일의 jwt: secretKey: 값
        byte[] accessSecret = this.accessSecret.getBytes(StandardCharsets.UTF_8);

        // JWT를 생성하는 부분
        String JwtToken = Jwts.builder()
                .setClaims(claims)
                .setIssuedAt(new Date())
                .setExpiration(new Date(new Date().getTime() + this.ACCESS_TOKEN_EXPIRE_COUNT))
                .signWith(Keys.hmacShaKeyFor(accessSecret))
                .compact();
        System.out.println(JwtToken);
    }
    // eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3bGdqczA2MDZAZ21haWwuY29tIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sInVzZXJJZCI6MSwiaWF0IjoxNjk1MTE3NjE3LCJleHAiOjE2OTUxMTk0MTd9.FR99VdbiYqKtHYWC0P3HtWWyudMyuLGsayxFNOc88Uo

    @Test
    public void parseToken() throws Exception {
        byte[] accessSecret = this.accessSecret.getBytes(StandardCharsets.UTF_8);
        String jwtToken = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ3bGdqczA2MDZAZ21haWwuY29tIiwicm9sZXMiOlsiUk9MRV9VU0VSIl0sInVzZXJJZCI6MSwiaWF0IjoxNjk1MTE3NjE3LCJleHAiOjE2OTUxMTk0MTd9.FR99VdbiYqKtHYWC0P3HtWWyudMyuLGsayxFNOc88Uo";

        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(accessSecret))
                .build()
                .parseClaimsJws(jwtToken)
                .getBody();
        System.out.println(claims.getSubject());
        System.out.println(claims.get("roles"));
        System.out.println(claims.get("userId"));
        System.out.println(claims.getIssuedAt());
        System.out.println(claims.getExpiration());
    }


}


