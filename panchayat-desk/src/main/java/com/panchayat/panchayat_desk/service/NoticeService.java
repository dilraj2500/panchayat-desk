package com.panchayat.panchayat_desk.service;

import com.panchayat.panchayat_desk.model.Notice;
import com.panchayat.panchayat_desk.repository.NoticeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class NoticeService {

    private final NoticeRepository noticeRepository;

    public Notice save(Notice notice) {
        return noticeRepository.save(notice);
    }

    public Notice findById(Long id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found!"));
    }

    public List<Notice> findActiveBySociety(Long societyId) {
        return noticeRepository.findBySocietyIdAndIsActiveTrueOrderByCreatedAtDesc(societyId);
    }

    public List<Notice> findAllBySociety(Long societyId) {
        return noticeRepository.findBySocietyIdOrderByCreatedAtDesc(societyId);
    }

    public void deactivate(Long id) {
        Notice notice = findById(id);
        notice.setIsActive(false);
        noticeRepository.save(notice);
    }

    public void deleteById(Long id) {
        noticeRepository.deleteById(id);
    }
}
