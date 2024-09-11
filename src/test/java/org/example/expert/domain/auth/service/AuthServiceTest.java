package org.example.expert.domain.auth.service;

import org.example.expert.config.JwtUtil;
import org.example.expert.config.PasswordEncoder;
import org.example.expert.domain.auth.dto.request.SigninRequest;
import org.example.expert.domain.auth.dto.request.SignupRequest;
import org.example.expert.domain.auth.dto.response.SigninResponse;
import org.example.expert.domain.auth.dto.response.SignupResponse;
import org.example.expert.domain.auth.exception.AuthException;
import org.example.expert.domain.common.exception.InvalidRequestException;
import org.example.expert.domain.user.entity.User;
import org.example.expert.domain.user.enums.UserRole;
import org.example.expert.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
public class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtUtil jwtUtil;
    @InjectMocks
    private AuthService authService;

    @Test
    public void signup_중복된_이메일로_에러가_발생한다() {
        // given
        SignupRequest signupRequest = new SignupRequest("test@example.com", "password", "USER");

        // 이미 존재하는 이메일
        given(userRepository.existsByEmail(anyString())).willReturn(true);

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.signup(signupRequest));

        assertEquals("이미 존재하는 이메일입니다.", exception.getMessage());
    }

    @Test
    public void signup_성공한다() {
        // given
        SignupRequest signupRequest = new SignupRequest("test@example.com", "password", "USER");

        // 새로운 사용자 생성
        given(userRepository.existsByEmail(anyString())).willReturn(false);
        given(passwordEncoder.encode(anyString())).willReturn("encodedPassword");
        User newUser = new User("test@example.com", "encodedPassword", UserRole.USER);
        given(userRepository.save(any(User.class))).willReturn(newUser);
        given(jwtUtil.createToken(newUser.getId(), newUser.getEmail(), newUser.getUserRole())).willReturn("token");

        // when
        SignupResponse response = authService.signup(signupRequest);

        // then
        assertNotNull(response);
        assertEquals("token", response.getBearerToken());
    }

    @Test
    public void signin_유저를_찾지_못해_에러가_발생한다() {
        // given
        SigninRequest request = new SigninRequest("test@example.com", "password");

        // 유저를 찾지 못함
        given(userRepository.findByEmail(anyString())).willReturn(Optional.empty());

        // when & then
        InvalidRequestException exception = assertThrows(InvalidRequestException.class, () -> authService.signin(request));
        assertEquals("가입되지 않은 유저입니다.", exception.getMessage());
    }

    @Test
    public void signin_잘못된_비밀번호로_에러가_발생한다() {
        // given
        SigninRequest request = new SigninRequest("test@example.com", "password");
        User user = new User("test@example.com", "encodedPassword", UserRole.USER);

        // 비밀번호 불일치
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(false);

        // when & then
        AuthException exception = assertThrows(AuthException.class, () -> authService.signin(request));
        assertEquals("잘못된 비밀번호입니다.", exception.getMessage());
    }

    @Test
    public void signin_성공한다() {
        // given
        SigninRequest request = new SigninRequest("test@example.com", "password");
        User user = new User("test@example.com", "encodedPassword", UserRole.USER);

        // 로그인 성공
        given(userRepository.findByEmail(anyString())).willReturn(Optional.of(user));
        given(passwordEncoder.matches(anyString(), anyString())).willReturn(true);
        given(jwtUtil.createToken(user.getId(), user.getEmail(), user.getUserRole())).willReturn("token");

        // when
        SigninResponse response = authService.signin(request);

        // then
        assertNotNull(response);
        assertEquals("token", response.getBearerToken());
    }
}