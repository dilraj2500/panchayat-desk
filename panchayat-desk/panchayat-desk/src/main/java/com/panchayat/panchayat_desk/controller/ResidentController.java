package com.panchayat.panchayat_desk.controller;

import com.panchayat.panchayat_desk.model.Complaint;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.service.ComplaintService;
import com.panchayat.panchayat_desk.service.EmailService;
import com.panchayat.panchayat_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/resident")
@RequiredArgsConstructor
public class ResidentController {

    private final UserService userService;
    private final ComplaintService complaintService;
    private final EmailService emailService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("complaints", complaintService.findByResident(user.getId()));
        model.addAttribute("pendingCount", complaintService.findBySocietyAndStatus(
                user.getSociety().getId(), Complaint.Status.PENDING).size());
        model.addAttribute("resolvedCount", complaintService.findBySocietyAndStatus(
                user.getSociety().getId(), Complaint.Status.RESOLVED).size());
        return "resident/dashboard";
    }

    @GetMapping("/complaint/new")
    public String newComplaintPage(Model model,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("complaint", new Complaint());
        model.addAttribute("categories", Complaint.Category.values());
        return "resident/new-complaint";
    }

    @PostMapping("/complaint/submit")
    public String submitComplaint(@ModelAttribute Complaint complaint,
                                  @AuthenticationPrincipal UserDetails userDetails,
                                  Model model) {
        try {
            User user = userService.findByEmail(userDetails.getUsername());
            complaint.setResident(user);
            complaint.setSociety(user.getSociety());
            complaintService.submit(complaint);

            // Email notification to secretary
            emailService.sendComplaintNotification(
                    user.getSociety().getSecretaryEmail(),
                    complaint.getTitle()
            );

            return "redirect:/resident/dashboard?success=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            return "resident/new-complaint";
        }
    }

    @GetMapping("/complaints")
    public String myComplaints(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("complaints", complaintService.findByResident(user.getId()));
        return "resident/complaints";
    }

    @GetMapping("/complaint/{id}")
    public String viewComplaint(@PathVariable Long id, Model model,
                                @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("complaint", complaintService.findById(id));
        return "resident/complaint-detail";
    }
}