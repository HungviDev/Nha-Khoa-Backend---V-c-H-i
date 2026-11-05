package com.medical.management.users.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.medical.management.roles.dto.RoleResponse;
import com.medical.management.users.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {

    private Long id;
    private String fullname;
    private String phone;
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String address;
    private String avatar;
    private RoleResponse role;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    public static UserResponse fromEntity(User user) {
        if (user == null) {
            return null;
        }
        return UserResponse.builder()
                .id(user.getId())
                .fullname(user.getFullname())
                .phone(user.getPhone())
                .email(user.getEmail())
                .birthDate(user.getBirthDate())
                .address(user.getAddress())
                .avatar(user.getAvatar())
                .role(user.getRole() != null ? RoleResponse.fromEntity(user.getRole()) : null)
                .createdAt(user.getCreatedAt())
                .build();
    }
}
