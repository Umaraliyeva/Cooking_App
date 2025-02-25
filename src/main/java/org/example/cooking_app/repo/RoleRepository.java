package org.example.cooking_app.repo;

import org.example.cooking_app.entity.Role;
import org.example.cooking_app.enums.RoleName;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Integer> {
  Optional<Role> findByRoleName(RoleName roleName);
}