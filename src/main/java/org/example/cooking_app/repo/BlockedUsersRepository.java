package org.example.cooking_app.repo;

import org.example.cooking_app.entity.BlockedUsers;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BlockedUsersRepository extends JpaRepository<BlockedUsers, String> {
}