package com.panchayat.panchayat_desk.service;

import com.panchayat.panchayat_desk.model.ChatMessage;
import com.panchayat.panchayat_desk.model.Complaint;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.repository.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatMessageRepository chatMessageRepository;

    public ChatMessage sendMessage(Complaint complaint, User sender, String message) {
        ChatMessage chatMessage = ChatMessage.builder()
                .complaint(complaint)
                .sender(sender)
                .message(message)
                .isResolvedMessage(false)
                .build();
        return chatMessageRepository.save(chatMessage);
    }

    public ChatMessage sendResolvedMessage(Complaint complaint, User sender) {
        ChatMessage chatMessage = ChatMessage.builder()
                .complaint(complaint)
                .sender(sender)
                .message("✅ Complaint has been RESOLVED!")
                .isResolvedMessage(true)
                .build();
        return chatMessageRepository.save(chatMessage);
    }

    public List<ChatMessage> getMessages(Long complaintId) {
        return chatMessageRepository.findByComplaintIdOrderByCreatedAtAsc(complaintId);
    }
}