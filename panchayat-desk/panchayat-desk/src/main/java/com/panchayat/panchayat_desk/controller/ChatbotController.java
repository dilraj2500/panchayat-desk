package com.panchayat.panchayat_desk.controller;

import com.panchayat.panchayat_desk.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/ask")
    public ResponseEntity<Map<String, String>> ask(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        String response = chatbotService.getResponse(message);
        return ResponseEntity.ok(Map.of("reply", response));
    }
}