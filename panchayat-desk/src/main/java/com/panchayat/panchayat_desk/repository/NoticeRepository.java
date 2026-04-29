package com.panchayat.panchayat_desk.repository;

import com.panchayat.panchayat_desk.model.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface NoticeRepository extends JpaRepository<Notice, Long> {
    List<Notice> findBySocietyIdAndIsActiveTrueOrderByCreatedAtDesc(Long societyId);
    List<Notice> findBySocietyIdOrderByCreatedAtDesc(Long societyId);
}
