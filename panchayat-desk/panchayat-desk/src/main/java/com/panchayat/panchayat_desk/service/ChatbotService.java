package com.panchayat.panchayat_desk.service;

import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatbotService {

    private final ChatClient.Builder chatClientBuilder;

    public String getResponse(String userMessage) {
        try {
            ChatClient chatClient = chatClientBuilder.build();
            return chatClient.prompt()
                    .system("""
                            You are a helpful assistant for PanchayatDesk — 
                            a Society Complaint Management System.
                            Help residents with their complaints, 
                            guide them how to submit complaints,
                            check status, and answer society related queries.
                            Be polite, helpful and respond in Hindi or English 
                            based on user's language.
                            Keep responses short and clear.
                            """)
                    .user(userMessage)
                    .call()
                    .content();
        } catch (Exception e) {
            return "Sorry, I am unable to respond right now. Please try again later.";
        }
    }
}