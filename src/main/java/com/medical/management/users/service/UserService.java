package com.medical.management.users.service;

import com.medical.management.common.response.PageResponse;
import com.medical.management.users.dto.UserCreateRequest;
import com.medical.management.users.dto.UserResponse;
import com.medical.management.users.dto.UserUpdateRequest;
import org.springframework.data.domain.Pageable;

public interface UserService {

    PageResponse<UserResponse> getUsers(String keyword, Pageable pageable);

    UserResponse getUserById(Long id);

    UserResponse getUserByEmail(String email);

    UserResponse getUserByPhone(String phone);

    UserResponse createUser(UserCreateRequest request);

    UserResponse updateUser(Long id, UserUpdateRequest request);

    void deleteUser(Long id);
}
