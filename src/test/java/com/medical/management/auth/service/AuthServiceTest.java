package com.medical.management.auth.service;

import com.medical.management.auth.dto.AuthResponse;
import com.medical.management.auth.dto.LoginRequest;
import com.medical.management.auth.dto.RegisterRequest;
import com.medical.management.common.constants.AppConstants;
import com.medical.management.common.exception.BadRequestException;
import com.medical.management.roles.entity.Role;
import com.medical.management.roles.repository.RoleRepository;
import com.medical.management.security.jwt.JwtTokenProvider;
import com.medical.management.security.service.CustomUserDetailsService;
import com.medical.management.security.service.UserPrincipal;
import com.medical.management.users.entity.User;
import com.medical.management.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider tokenProvider;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @InjectMocks
    private AuthServiceImpl authService;

    private Role patientRole;
    private User testUser;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "jwtExpirationMs", 86400000L);

        patientRole = Role.builder()
                .id(3L)
                .name(AppConstants.ROLE_PATIENT)
                .build();

        testUser = User.builder()
                .id(1L)
                .fullname("Nguyen Van A")
                .email("nguyenvana@gmail.com")
                .phone("0987654321")
                .password("encoded_pass")
                .birthDate(LocalDate.of(1995, 5, 20))
                .address("Ha Noi")
                .role(patientRole)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should successfully authenticate user and return tokens on login")
    void login_Success() {
        LoginRequest request = new LoginRequest("nguyenvana@gmail.com", "password123");
        UserPrincipal principal = UserPrincipal.create(testUser);
        Authentication authentication = mock(Authentication.class);

        when(authentication.getPrincipal()).thenReturn(principal);
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class))).thenReturn(authentication);
        when(tokenProvider.generateAccessToken(principal)).thenReturn("mock_access_token");
        when(tokenProvider.generateRefreshToken(principal)).thenReturn("mock_refresh_token");
        when(userRepository.findByIdWithRole(1L)).thenReturn(Optional.of(testUser));

        AuthResponse response = authService.login(request);

        assertNotNull(response);
        assertEquals("mock_access_token", response.getAccessToken());
        assertEquals("mock_refresh_token", response.getRefreshToken());
        assertEquals("Bearer", response.getTokenType());
        assertEquals("nguyenvana@gmail.com", response.getUser().getEmail());
    }

    @Test
    @DisplayName("Should successfully register a new patient account")
    void register_Success() {
        RegisterRequest request = RegisterRequest.builder()
                .fullname("Tran Thi B")
                .email("tranthib@gmail.com")
                .phone("0912345678")
                .password("secret123")
                .birthDate(LocalDate.of(2000, 1, 1))
                .address("Da Nang")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(request.getPhone())).thenReturn(false);
        when(roleRepository.findByName(AppConstants.ROLE_PATIENT)).thenReturn(Optional.of(patientRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_secret");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(2L);
            u.setCreatedAt(LocalDateTime.now());
            return u;
        });
        when(tokenProvider.generateAccessToken(any(UserPrincipal.class))).thenReturn("access_token_123");
        when(tokenProvider.generateRefreshToken(any(UserPrincipal.class))).thenReturn("refresh_token_123");

        AuthResponse response = authService.register(request);

        assertNotNull(response);
        assertEquals("access_token_123", response.getAccessToken());
        assertEquals("tranthib@gmail.com", response.getUser().getEmail());
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException if email already registered")
    void register_DuplicateEmail_ThrowsException() {
        RegisterRequest request = RegisterRequest.builder()
                .fullname("Duplicate User")
                .email("nguyenvana@gmail.com")
                .phone("0999999999")
                .password("pass123")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
    }

    @Test
    @DisplayName("Should throw BadRequestException if phone number already registered")
    void register_DuplicatePhone_ThrowsException() {
        RegisterRequest request = RegisterRequest.builder()
                .fullname("Duplicate Phone User")
                .email("newemail@gmail.com")
                .phone("0987654321")
                .password("pass123")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(request.getPhone())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> authService.register(request));
    }
}
