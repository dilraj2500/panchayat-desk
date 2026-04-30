package com.panchayat.panchayat_desk.repository;

import com.panchayat.panchayat_desk.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessage, Long> {
    List<ChatMessage> findByComplaintIdOrderByCreatedAtAsc(Long complaintId);
}