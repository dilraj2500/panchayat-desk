package com.panchayat.panchayat_desk.controller;

import com.panchayat.panchayat_desk.model.MaintenanceBill;
import com.panchayat.panchayat_desk.model.Payment;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.service.MaintenanceBillService;
import com.panchayat.panchayat_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Controller
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceBillService billService;
    private final UserService userService;

    // ---- SECRETARY ----

    @GetMapping("/secretary/maintenance")
    public String secretaryMaintenance(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        Long societyId = user.getSociety().getId();
        model.addAttribute("user", user);
        model.addAttribute("bills", billService.findBySociety(societyId));
        model.addAttribute("unpaidCount", billService.countUnpaidBySociety(societyId));
        model.addAttribute("residents",
                userService.findBySociety(societyId).stream()
                        .filter(u -> u.getRole() == User.Role.RESIDENT).toList());
        return "secretary/maintenance";
    }

    @PostMapping("/secretary/maintenance/generate")
    public String generateBills(@AuthenticationPrincipal UserDetails userDetails,
                                @RequestParam BigDecimal amount,
                                @RequestParam String billMonth,
                                @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate dueDate) {
        User user = userService.findByEmail(userDetails.getUsername());
        List<User> residents = userService.findBySociety(user.getSociety().getId())
                .stream().filter(u -> u.getRole() == User.Role.RESIDENT).toList();
        billService.generateBillsForSociety(residents, user.getSociety().getId(), amount, billMonth, dueDate);
        return "redirect:/secretary/maintenance?generated=true";
    }

    // ---- RESIDENT ----

    @GetMapping("/resident/maintenance")
    public String residentMaintenance(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("bills", billService.findByResident(user.getId()));
        model.addAttribute("payments", billService.getPaymentHistory(user.getId()));
        model.addAttribute("methods", Payment.PaymentMethod.values());
        return "resident/maintenance";
    }

    @PostMapping("/resident/maintenance/pay/{billId}")
    public String payBill(@PathVariable Long billId,
                          @RequestParam Payment.PaymentMethod method,
                          @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        billService.payBill(billId, user, method);
        return "redirect:/resident/maintenance?paid=true";
    }
}
