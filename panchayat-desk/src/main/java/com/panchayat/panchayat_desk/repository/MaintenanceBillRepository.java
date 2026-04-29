package com.panchayat.panchayat_desk.repository;

import com.panchayat.panchayat_desk.model.MaintenanceBill;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MaintenanceBillRepository extends JpaRepository<MaintenanceBill, Long> {
    List<MaintenanceBill> findByResidentId(Long residentId);
    List<MaintenanceBill> findBySocietyId(Long societyId);
    List<MaintenanceBill> findBySocietyIdAndStatus(Long societyId, MaintenanceBill.BillStatus status);
    List<MaintenanceBill> findByResidentIdAndStatus(Long residentId, MaintenanceBill.BillStatus status);
    long countBySocietyIdAndStatus(Long societyId, MaintenanceBill.BillStatus status);
}
