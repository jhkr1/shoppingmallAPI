package com.jhkr1.shopping.mall_api.controller;

import com.jhkr1.shopping.mall_api.dto.MemberLoginDto;
import com.jhkr1.shopping.mall_api.dto.MemberLoginResponseDto;
import com.jhkr1.shopping.mall_api.security.jwt.util.JwtTokenizer;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequiredArgsConstructor
@Validated
public class MemberController {

    private final JwtTokenizer jwtTokenizer;

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid MemberLoginDto loginDto) {
        Long memberId = 1L;
        String email = loginDto.getEmail();
        List<String> roles = List.of("ROLE_USER");

        String accessToken = jwtTokenizer.createAccessToken(memberId, email, roles);
        String refreshToken = jwtTokenizer.createRefreshToken(memberId, email, roles);

        MemberLoginResponseDto loginResponse = MemberLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(memberId)
                .nickname("nickname")
                .build();

        return new ResponseEntity(loginResponse, HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String token) {
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
