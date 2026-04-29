package com.panchayat.panchayat_desk.service;

import com.panchayat.panchayat_desk.model.Flat;
import com.panchayat.panchayat_desk.model.Society;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.repository.FlatRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class FlatService {

    private final FlatRepository flatRepository;

    public Flat save(Flat flat) {
        if (flatRepository.existsBySocietyIdAndFlatNumber(
                flat.getSociety().getId(), flat.getFlatNumber())) {
            throw new RuntimeException("Flat number already exists in this society!");
        }
        return flatRepository.save(flat);
    }

    public Flat findById(Long id) {
        return flatRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flat not found!"));
    }

    public List<Flat> findBySociety(Long societyId) {
        return flatRepository.findBySocietyId(societyId);
    }

    public Flat update(Flat flat) {
        return flatRepository.save(flat);
    }

    public void deleteById(Long id) {
        flatRepository.deleteById(id);
    }
}
