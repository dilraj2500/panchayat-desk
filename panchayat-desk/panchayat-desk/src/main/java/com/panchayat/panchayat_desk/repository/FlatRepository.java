package com.panchayat.panchayat_desk.repository;

import com.panchayat.panchayat_desk.model.Flat;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface FlatRepository extends JpaRepository<Flat, Long> {
    List<Flat> findBySocietyId(Long societyId);
    Optional<Flat> findBySocietyIdAndFlatNumber(Long societyId, String flatNumber);
    boolean existsBySocietyIdAndFlatNumber(Long societyId, String flatNumber);
}
