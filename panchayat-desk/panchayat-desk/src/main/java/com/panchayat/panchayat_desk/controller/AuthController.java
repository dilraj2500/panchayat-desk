package com.panchayat.panchayat_desk.controller;

import com.panchayat.panchayat_desk.model.Society;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.service.SocietyService;
import com.panchayat.panchayat_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final SocietyService societyService;

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage(Model model) {
        model.addAttribute("user", new User());
        model.addAttribute("societies", societyService.findAll());
        return "auth/register";
    }

    @PostMapping("/register")
    public String register(@ModelAttribute User user,
                           @RequestParam Long societyId,
                           Model model) {
        try {
            Society society = societyService.findById(societyId);
            user.setSociety(society);
            user.setRole(User.Role.RESIDENT);
            userService.register(user);
            return "redirect:/login?registered=true";
        } catch (Exception e) {
            model.addAttribute("error", e.getMessage());
            model.addAttribute("societies", societyService.findAll());
            return "auth/register";
        }
    }

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        if (user.getRole() == User.Role.ADMIN) {
            return "redirect:/admin/dashboard";
        } else if (user.getRole() == User.Role.SECRETARY) {
            return "redirect:/secretary/dashboard";
        } else {
            return "redirect:/resident/dashboard";
        }
    }
}