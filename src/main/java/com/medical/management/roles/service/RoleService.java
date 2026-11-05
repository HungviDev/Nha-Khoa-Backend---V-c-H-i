package com.medical.management.roles.service;

import com.medical.management.roles.dto.RoleRequest;
import com.medical.management.roles.dto.RoleResponse;

import java.util.List;

public interface RoleService {

    List<RoleResponse> getAllRoles();

    RoleResponse getRoleById(Long id);

    RoleResponse getRoleByName(String name);

    RoleResponse createRole(RoleRequest request);

    RoleResponse updateRole(Long id, RoleRequest request);

    void deleteRole(Long id);
}
