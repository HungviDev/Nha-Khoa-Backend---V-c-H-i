package com.medical.management.roles.service;

import com.medical.management.common.exception.BadRequestException;
import com.medical.management.common.exception.ResourceNotFoundException;
import com.medical.management.roles.dto.RoleRequest;
import com.medical.management.roles.dto.RoleResponse;
import com.medical.management.roles.entity.Role;
import com.medical.management.roles.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> getAllRoles() {
        return roleRepository.findAll()
                .stream()
                .map(RoleResponse::fromEntity)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleById(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        return RoleResponse.fromEntity(role);
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse getRoleByName(String name) {
        Role role = roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "name", name));
        return RoleResponse.fromEntity(role);
    }

    @Override
    @Transactional
    public RoleResponse createRole(RoleRequest request) {
        String roleName = request.getName().trim().toUpperCase();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        if (roleRepository.existsByName(roleName)) {
            throw new BadRequestException("Role name already exists: " + roleName);
        }

        Role role = Role.builder()
                .name(roleName)
                .build();

        Role saved = roleRepository.save(role);
        return RoleResponse.fromEntity(saved);
    }

    @Override
    @Transactional
    public RoleResponse updateRole(Long id, RoleRequest request) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));

        String roleName = request.getName().trim().toUpperCase();
        if (!roleName.startsWith("ROLE_")) {
            roleName = "ROLE_" + roleName;
        }

        if (!role.getName().equalsIgnoreCase(roleName) && roleRepository.existsByName(roleName)) {
            throw new BadRequestException("Role name already exists: " + roleName);
        }

        role.setName(roleName);
        Role updated = roleRepository.save(role);
        return RoleResponse.fromEntity(updated);
    }

    @Override
    @Transactional
    public void deleteRole(Long id) {
        Role role = roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Role", "id", id));
        roleRepository.delete(role);
    }
}
