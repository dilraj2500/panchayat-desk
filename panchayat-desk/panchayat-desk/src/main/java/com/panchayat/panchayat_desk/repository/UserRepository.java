package com.panchayat.panchayat_desk.repository;

import com.panchayat.panchayat_desk.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);
    List<User> findBySocietyId(Long societyId);
    List<User> findBySocietyIdAndRole(Long societyId, User.Role role);
}