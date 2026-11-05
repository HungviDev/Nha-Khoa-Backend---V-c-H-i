package com.medical.management.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TokenPayload {

    private Long userId;
    private String email;
    private String fullname;
    private List<String> roles;
}
