package com.medical.management.users.service;

import com.medical.management.common.constants.AppConstants;
import com.medical.management.common.exception.BadRequestException;
import com.medical.management.common.exception.ResourceNotFoundException;
import com.medical.management.roles.entity.Role;
import com.medical.management.roles.repository.RoleRepository;
import com.medical.management.users.dto.UserCreateRequest;
import com.medical.management.users.dto.UserResponse;
import com.medical.management.users.entity.User;
import com.medical.management.users.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private RoleRepository roleRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserServiceImpl userService;

    private Role doctorRole;
    private User doctorUser;

    @BeforeEach
    void setUp() {
        doctorRole = Role.builder()
                .id(2L)
                .name(AppConstants.ROLE_DOCTOR)
                .build();

        doctorUser = User.builder()
                .id(10L)
                .fullname("BS. Nguyen Van C")
                .email("dr.nguyenvanc@hospital.com")
                .phone("0933333333")
                .password("encoded_pass")
                .birthDate(LocalDate.of(1985, 3, 15))
                .address("TP. Ho Chi Minh")
                .role(doctorRole)
                .createdAt(LocalDateTime.now())
                .build();
    }

    @Test
    @DisplayName("Should create user successfully with encrypted password")
    void createUser_Success() {
        UserCreateRequest request = UserCreateRequest.builder()
                .fullname("BS. Nguyen Van C")
                .email("dr.nguyenvanc@hospital.com")
                .phone("0933333333")
                .password("doctorpass123")
                .birthDate(LocalDate.of(1985, 3, 15))
                .address("TP. Ho Chi Minh")
                .roleId(2L)
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(false);
        when(userRepository.existsByPhone(request.getPhone())).thenReturn(false);
        when(roleRepository.findById(2L)).thenReturn(Optional.of(doctorRole));
        when(passwordEncoder.encode(request.getPassword())).thenReturn("encoded_pass");
        when(userRepository.save(any(User.class))).thenAnswer(inv -> {
            User u = inv.getArgument(0);
            u.setId(10L);
            u.setCreatedAt(LocalDateTime.now());
            return u;
        });

        UserResponse response = userService.createUser(request);

        assertNotNull(response);
        assertEquals(10L, response.getId());
        assertEquals("BS. Nguyen Van C", response.getFullname());
        assertEquals("dr.nguyenvanc@hospital.com", response.getEmail());
        verify(passwordEncoder).encode("doctorpass123");
        verify(userRepository).save(any(User.class));
    }

    @Test
    @DisplayName("Should throw BadRequestException if email already taken")
    void createUser_DuplicateEmail_ThrowsException() {
        UserCreateRequest request = UserCreateRequest.builder()
                .email("dr.nguyenvanc@hospital.com")
                .phone("0988888888")
                .password("pass123")
                .build();

        when(userRepository.existsByEmail(request.getEmail())).thenReturn(true);

        assertThrows(BadRequestException.class, () -> userService.createUser(request));
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when user id not found")
    void getUserById_NotFound_ThrowsException() {
        when(userRepository.findByIdWithRole(999L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> userService.getUserById(999L));
    }
}
