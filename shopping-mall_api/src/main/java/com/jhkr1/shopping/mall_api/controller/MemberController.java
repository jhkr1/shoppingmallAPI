package com.jhkr1.shopping.mall_api.controller;

import com.jhkr1.shopping.mall_api.domain.Member;
import com.jhkr1.shopping.mall_api.domain.RefreshToken;
import com.jhkr1.shopping.mall_api.domain.Role;
import com.jhkr1.shopping.mall_api.dto.MemberLoginDto;
import com.jhkr1.shopping.mall_api.dto.MemberLoginResponseDto;
import com.jhkr1.shopping.mall_api.dto.MemberSignupDto;
import com.jhkr1.shopping.mall_api.dto.MemberSignupResponseDto;
import com.jhkr1.shopping.mall_api.security.jwt.util.JwtTokenizer;
import com.jhkr1.shopping.mall_api.service.MemberService;
import com.jhkr1.shopping.mall_api.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
@Validated
@RequestMapping("/members")
public class MemberController {

    private final JwtTokenizer jwtTokenizer;
    private final MemberService memberService;
    private final RefreshTokenService refreshTokenService;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/signup")
    public ResponseEntity signup(@RequestBody @Valid MemberSignupDto memberSignupDto, BindingResult bindingResult){
        if(bindingResult.hasErrors()) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        Member member = new Member();
        member.setName(memberSignupDto.getName());
        member.setEmail(memberSignupDto.getEmail());
        member.setPassword(passwordEncoder.encode(memberSignupDto.getPassword()));

        Member saveMember = memberService.addMember(member);

        MemberSignupResponseDto memberSignupResponse = new MemberSignupResponseDto();
        memberSignupResponse.setMemberId(saveMember.getMemberId());
        memberSignupResponse.setName(saveMember.getName());
        memberSignupResponse.setRegdate(saveMember.getRegdate());
        memberSignupResponse.setEmail(saveMember.getEmail());

        return new ResponseEntity(memberSignupResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity login(@RequestBody @Valid MemberLoginDto loginDto) {
        Member member = memberService.findByEmail(loginDto.getEmail());
        if(!passwordEncoder.matches(loginDto.getPassword(), member.getPassword())){
            return new ResponseEntity(HttpStatus.UNAUTHORIZED);
        }

        List<String> roles = member.getRoles().stream().map(Role::getName).collect(Collectors.toList());

        String accessToken = jwtTokenizer.createAccessToken(member.getMemberId(), member.getEmail(), roles);
        String refreshToken = jwtTokenizer.createRefreshToken(member.getMemberId(), member.getEmail(), roles);

        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setValue(refreshToken);
        refreshTokenEntity.setMemberId(member.getMemberId());
        refreshTokenService.addRefreshToken(refreshTokenEntity);

        MemberLoginResponseDto loginResponse = MemberLoginResponseDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .memberId(member.getMemberId())
                .nickname(member.getName())
                .build();


        return new ResponseEntity(loginResponse, HttpStatus.OK);
    }

    @DeleteMapping("/logout")
    public ResponseEntity logout(@RequestHeader("Authorization") String token) {
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }
}
