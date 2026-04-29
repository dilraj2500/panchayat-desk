package com.panchayat.panchayat_desk.repository;

import com.panchayat.panchayat_desk.model.Society;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;

public interface SocietyRepository extends JpaRepository<Society, Long> {
    Optional<Society> findBySecretaryEmail(String email);

    List<Society> findByCity(String city);

    boolean existsByName(String name);
}