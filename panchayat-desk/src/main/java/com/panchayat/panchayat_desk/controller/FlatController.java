package com.panchayat.panchayat_desk.controller;

import com.panchayat.panchayat_desk.model.Flat;
import com.panchayat.panchayat_desk.model.Society;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.service.FlatService;
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
public class FlatController {

    private final FlatService flatService;
    private final UserService userService;
    private final SocietyService societyService;

    // ---- SECRETARY manages own society's flats ----

    @GetMapping("/secretary/flats")
    public String secretaryFlats(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        Long societyId = user.getSociety().getId();
        model.addAttribute("user", user);
        model.addAttribute("flats", flatService.findBySociety(societyId));
        model.addAttribute("newFlat", new Flat());
        model.addAttribute("types", Flat.OccupancyType.values());
        model.addAttribute("residents",
                userService.findBySociety(societyId).stream()
                        .filter(u -> u.getRole() == User.Role.RESIDENT).toList());
        return "secretary/flats";
    }

    @PostMapping("/secretary/flat/add")
    public String addFlat(@ModelAttribute Flat flat,
                          @AuthenticationPrincipal UserDetails userDetails,
                          @RequestParam(required = false) Long ownerId,
                          @RequestParam(required = false) Long tenantId) {
        User user = userService.findByEmail(userDetails.getUsername());
        flat.setSociety(user.getSociety());
        if (ownerId != null) flat.setOwner(userService.findById(ownerId));
        if (tenantId != null) flat.setTenant(userService.findById(tenantId));
        flatService.save(flat);
        return "redirect:/secretary/flats?added=true";
    }

    @PostMapping("/secretary/flat/{id}/delete")
    public String deleteFlat(@PathVariable Long id) {
        flatService.deleteById(id);
        return "redirect:/secretary/flats?deleted=true";
    }

    // ---- ADMIN can view flats of any society ----

    @GetMapping("/admin/society/{societyId}/flats")
    public String adminFlats(@PathVariable Long societyId, Model model) {
        Society society = societyService.findById(societyId);
        model.addAttribute("society", society);
        model.addAttribute("flats", flatService.findBySociety(societyId));
        return "admin/flats";
    }
}
