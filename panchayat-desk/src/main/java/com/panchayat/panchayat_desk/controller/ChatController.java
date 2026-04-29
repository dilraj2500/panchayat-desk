package com.panchayat.panchayat_desk.controller;

import com.panchayat.panchayat_desk.model.ChatMessage;
import com.panchayat.panchayat_desk.model.Complaint;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.service.ChatService;
import com.panchayat.panchayat_desk.service.ComplaintService;
import com.panchayat.panchayat_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;
    private final ComplaintService complaintService;
    private final UserService userService;
    private final SimpMessagingTemplate messagingTemplate;

    @PostMapping("/send/{complaintId}")
    public ResponseEntity<ChatMessage> sendMessage(
            @PathVariable Long complaintId,
            @RequestBody Map<String, String> body,
            @AuthenticationPrincipal UserDetails userDetails) {

        User sender = userService.findByEmail(userDetails.getUsername());
        Complaint complaint = complaintService.findById(complaintId);
        String message = body.get("message");

        ChatMessage chatMessage = chatService.sendMessage(complaint, sender, message);

        // WebSocket se real-time message bhejo
        messagingTemplate.convertAndSend(
                "/topic/complaint/" + complaintId,
                Map.of(
                        "senderName", sender.getName(),
                        "senderRole", sender.getRole().name(),
                        "message", message,
                        "isResolved", false,
                        "time", chatMessage.getCreatedAt().toString()
                )
        );

        return ResponseEntity.ok(chatMessage);
    }

    @GetMapping("/messages/{complaintId}")
    public ResponseEntity<List<ChatMessage>> getMessages(@PathVariable Long complaintId) {
        return ResponseEntity.ok(chatService.getMessages(complaintId));
    }
}