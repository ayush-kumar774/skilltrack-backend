package org.havoc.skilltrack.user.repository;

import org.havoc.skilltrack.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    Optional<User> findByUsername(String username);
    boolean existsByUsername(String username);
}
