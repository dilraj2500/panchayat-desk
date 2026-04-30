package com.panchayat.panchayat_desk.repository;

import com.panchayat.panchayat_desk.model.Complaint;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComplaintRepository extends JpaRepository<Complaint, Long> {


    List<Complaint> findByResidentId(Long id);


    List<Complaint> findBySocietyId(Long id);

    List<Complaint> findBySocietyIdAndStatus(Long societyId, Complaint.Status status);


    long countBySocietyIdAndStatus(Long societyId, Complaint.Status status);
}