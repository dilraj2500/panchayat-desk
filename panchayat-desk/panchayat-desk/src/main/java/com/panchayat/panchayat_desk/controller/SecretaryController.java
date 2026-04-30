package com.panchayat.panchayat_desk.controller;

import com.panchayat.panchayat_desk.model.Complaint;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.service.ChatService;
import com.panchayat.panchayat_desk.service.ComplaintService;
import com.panchayat.panchayat_desk.service.EmailService;
import com.panchayat.panchayat_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@Controller
@RequestMapping("/secretary")
@RequiredArgsConstructor
public class SecretaryController {

    private final UserService userService;
    private final ComplaintService complaintService;
    private final EmailService emailService;
    private final ChatService chatService;
    private final SimpMessagingTemplate messagingTemplate;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        Long societyId = user.getSociety().getId();
        model.addAttribute("user", user);
        model.addAttribute("complaints", complaintService.findBySociety(societyId));
        model.addAttribute("pendingCount", complaintService.countByStatus(societyId, Complaint.Status.PENDING));
        model.addAttribute("inProgressCount", complaintService.countByStatus(societyId, Complaint.Status.IN_PROGRESS));
        model.addAttribute("resolvedCount", complaintService.countByStatus(societyId, Complaint.Status.RESOLVED));
        model.addAttribute("totalResidents", userService.findBySociety(societyId).size());
        return "secretary/dashboard";
    }

    @GetMapping("/complaints")
    public String allComplaints(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam(required = false) String status, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        Long societyId = user.getSociety().getId();
        model.addAttribute("user", user);
        model.addAttribute("statuses", Complaint.Status.values());
        model.addAttribute("selectedStatus", status);

        if (status != null && !status.isEmpty()) {
            model.addAttribute("complaints", complaintService.findBySocietyAndStatus(
                    societyId, Complaint.Status.valueOf(status)));
        } else {
            model.addAttribute("complaints", complaintService.findBySociety(societyId));
        }
        return "secretary/complaints";
    }

    @GetMapping("/complaint/{id}")
    public String viewComplaint(@PathVariable Long id, Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("complaint", complaintService.findById(id));
        model.addAttribute("messages", chatService.getMessages(id));
        model.addAttribute("statuses", Complaint.Status.values());
        model.addAttribute("priorities", Complaint.Priority.values());
        return "secretary/complaint-detail";
    }

    @PostMapping("/complaint/{id}/update")
    public String updateComplaint(@PathVariable Long id,
                                  @RequestParam Complaint.Status status,
                                  @RequestParam String reply,
                                  @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        Complaint complaint = complaintService.updateStatus(id, status, reply);

        // Chat mein message save karo
        chatService.sendMessage(complaint, user, reply);

        // WebSocket notification
        messagingTemplate.convertAndSend(
                "/topic/complaint/" + id,
                Map.of(
                        "senderName", user.getName(),
                        "senderRole", user.getRole().name(),
                        "message", reply,
                        "isResolved", false,
                        "time", java.time.LocalDateTime.now().toString()
                )
        );

        emailService.sendStatusUpdate(
                complaint.getResident().getEmail(),
                complaint.getTitle(),
                status.name()
        );
        return "redirect:/secretary/complaints?updated=true";
    }

    @PostMapping("/complaint/{id}/resolve")
    public String resolveComplaint(@PathVariable Long id,
                                   @RequestParam String reply,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        Complaint complaint = complaintService.updateStatus(id, Complaint.Status.RESOLVED, reply);

        // Chat mein reply save karo
        chatService.sendMessage(complaint, user, reply);

        // Resolved message save karo
        chatService.sendResolvedMessage(complaint, user);

        // WebSocket — reply message
        messagingTemplate.convertAndSend(
                "/topic/complaint/" + id,
                Map.of(
                        "senderName", user.getName(),
                        "senderRole", "SECRETARY",
                        "message", reply,
                        "isResolved", false,
                        "time", java.time.LocalDateTime.now().toString()
                )
        );

        // WebSocket — resolved message
        messagingTemplate.convertAndSend(
                "/topic/complaint/" + id,
                Map.of(
                        "senderName", "System",
                        "senderRole", "SYSTEM",
                        "message", "✅ Complaint has been RESOLVED!",
                        "isResolved", true,
                        "time", java.time.LocalDateTime.now().toString()
                )
        );

        emailService.sendStatusUpdate(
                complaint.getResident().getEmail(),
                complaint.getTitle(),
                "RESOLVED"
        );
        return "redirect:/secretary/complaints?resolved=true";
    }

    @GetMapping("/residents")
    public String residents(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("residents", userService.findBySociety(user.getSociety().getId()));
        return "secretary/residents";
    }
}