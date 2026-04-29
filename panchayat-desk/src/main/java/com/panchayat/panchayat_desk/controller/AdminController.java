package com.panchayat.panchayat_desk.controller;

import com.panchayat.panchayat_desk.model.Complaint;
import com.panchayat.panchayat_desk.model.Society;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.service.ComplaintService;
import com.panchayat.panchayat_desk.service.EmailService;
import com.panchayat.panchayat_desk.service.SocietyService;
import com.panchayat.panchayat_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;
    private final SocietyService societyService;
    private final ComplaintService complaintService;
    private final EmailService emailService;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        List<Society> societies = societyService.findAll();

        Map<Long, Map<String, Long>> societyStats = new HashMap<>();
        long totalComplaints = 0;
        long totalResolved = 0;
        long totalUsers = 0;

        for (Society society : societies) {
            Map<String, Long> stats = new HashMap<>();
            stats.put("pending", complaintService.countByStatus(society.getId(), Complaint.Status.PENDING));
            stats.put("inProgress", complaintService.countByStatus(society.getId(), Complaint.Status.IN_PROGRESS));
            stats.put("resolved", complaintService.countByStatus(society.getId(), Complaint.Status.RESOLVED));
            societyStats.put(society.getId(), stats);

            totalComplaints += complaintService.findBySociety(society.getId()).size();
            totalResolved += stats.get("resolved");
            totalUsers += userService.findBySociety(society.getId()).size();
        }

        model.addAttribute("user", user);
        model.addAttribute("societies", societies);
        model.addAttribute("societyStats", societyStats);
        model.addAttribute("totalSocieties", societies.size());
        model.addAttribute("totalComplaints", totalComplaints);
        model.addAttribute("totalResolved", totalResolved);
        model.addAttribute("totalUsers", totalUsers);
        return "admin/dashboard";
    }

    @GetMapping("/complaint/{id}/details")
    @ResponseBody
    public Map<String, String> getComplaintDetails(@PathVariable Long id) {
        Complaint complaint = complaintService.findById(id);
        Map<String, String> details = new HashMap<>();
        details.put("title", complaint.getTitle());
        details.put("description", complaint.getDescription());
        details.put("category", complaint.getCategory().name());
        return details;
    }

    @PostMapping("/complaint/{id}/resolve")
    public String resolveComplaint(@PathVariable Long id,
                                   @RequestParam String reply,
                                   @AuthenticationPrincipal UserDetails userDetails) {
        Complaint complaint = complaintService.updateStatus(id, Complaint.Status.RESOLVED, reply);
        emailService.sendStatusUpdate(
                complaint.getResident().getEmail(),
                complaint.getTitle(),
                "RESOLVED"
        );
        return "redirect:/admin/dashboard?resolved=true";
    }

    @GetMapping("/societies")
    public String societies(Model model) {
        model.addAttribute("societies", societyService.findAll());
        model.addAttribute("newSociety", new Society());
        return "admin/societies";
    }

    @PostMapping("/society/add")
    public String addSociety(@ModelAttribute Society society,
                             @RequestParam String secretaryName,
                             @RequestParam String secretaryEmail,
                             @RequestParam String secretaryPassword) {
        society.setSecretaryEmail(secretaryEmail);
        societyService.save(society);

        User secretary = User.builder()
                .name(secretaryName)
                .email(secretaryEmail)
                .password(secretaryPassword)
                .role(User.Role.SECRETARY)
                .society(society)
                .build();
        userService.register(secretary);

        return "redirect:/admin/societies?added=true";
    }

    @GetMapping("/users")
    public String users(Model model) {
        model.addAttribute("users", userService.findAll());
        return "admin/users";
    }

    @PostMapping("/user/{id}/delete")
    public String deleteUser(@PathVariable Long id) {
        userService.deleteById(id);
        return "redirect:/admin/users?deleted=true";
    }
}