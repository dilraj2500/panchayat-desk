package com.panchayat.panchayat_desk.service;

import com.panchayat.panchayat_desk.model.Complaint;
import com.panchayat.panchayat_desk.repository.ComplaintRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ComplaintService {

    private final ComplaintRepository complaintRepository;


    public List<Complaint> findAll() {
        return complaintRepository.findAll();
    }


    public List<Complaint> findByResident(Long id) {
        return complaintRepository.findByResidentId(id);
    }


    public List<Complaint> findBySociety(Long id) {
        return complaintRepository.findBySocietyId(id);
    }


    public List<Complaint> findBySocietyAndStatus(Long societyId, Complaint.Status status) {
        return complaintRepository.findBySocietyIdAndStatus(societyId, status);
    }


    public Complaint submit(Complaint complaint) {
        return complaintRepository.save(complaint);
    }


    public long countByStatus(Long societyId, Complaint.Status status) {
        return complaintRepository.countBySocietyIdAndStatus(societyId, status);
    }


    public Complaint findById(Long id) {
        return complaintRepository.findById(id).orElse(null);
    }


    public Complaint updateStatus(Long id, Complaint.Status status, String reply) {

        Complaint complaint = complaintRepository.findById(id).orElse(null);

        if (complaint != null) {
            complaint.setStatus(status);
            complaint.setSecretaryReply(reply);

            if (status == Complaint.Status.RESOLVED) {
                complaint.setResolvedAt(LocalDateTime.now());
            }

            return complaintRepository.save(complaint);
        }

        return null;
    }
}