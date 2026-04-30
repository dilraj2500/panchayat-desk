package com.panchayat.panchayat_desk.service;

import com.panchayat.panchayat_desk.model.Society;
import com.panchayat.panchayat_desk.repository.SocietyRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class SocietyService {

    private final SocietyRepository societyRepository;

    public Society save(Society society) {
        if (societyRepository.existsByName(society.getName())) {
            throw new RuntimeException("Society already exists!");
        }
        return societyRepository.save(society);
    }

    public Society findById(Long id) {
        return societyRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Society not found!"));
    }

    public Society findBySecretaryEmail(String email) {
        return societyRepository.findBySecretaryEmail(email)
                .orElseThrow(() -> new RuntimeException("Society not found!"));
    }

    public List<Society> findAll() {
        return societyRepository.findAll();
    }

    public void deleteById(Long id) {
        societyRepository.deleteById(id);
    }
}