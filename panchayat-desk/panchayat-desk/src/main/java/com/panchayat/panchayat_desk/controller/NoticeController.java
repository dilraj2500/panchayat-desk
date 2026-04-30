package com.panchayat.panchayat_desk.controller;

import com.panchayat.panchayat_desk.model.Notice;
import com.panchayat.panchayat_desk.model.User;
import com.panchayat.panchayat_desk.service.NoticeService;
import com.panchayat.panchayat_desk.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
public class NoticeController {

    private final NoticeService noticeService;
    private final UserService userService;

    // ---- SECRETARY ----

    @GetMapping("/secretary/notices")
    public String secretaryNotices(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("notices", noticeService.findAllBySociety(user.getSociety().getId()));
        model.addAttribute("newNotice", new Notice());
        model.addAttribute("types", Notice.NoticeType.values());
        return "secretary/notices";
    }

    @PostMapping("/secretary/notice/add")
    public String addNotice(@ModelAttribute Notice notice,
                            @AuthenticationPrincipal UserDetails userDetails) {
        User user = userService.findByEmail(userDetails.getUsername());
        notice.setPostedBy(user);
        notice.setSociety(user.getSociety());
        noticeService.save(notice);
        return "redirect:/secretary/notices?added=true";
    }

    @PostMapping("/secretary/notice/{id}/delete")
    public String deleteNotice(@PathVariable Long id) {
        noticeService.deleteById(id);
        return "redirect:/secretary/notices?deleted=true";
    }

    // ---- RESIDENT ----

    @GetMapping("/resident/notices")
    public String residentNotices(@AuthenticationPrincipal UserDetails userDetails, Model model) {
        User user = userService.findByEmail(userDetails.getUsername());
        model.addAttribute("user", user);
        model.addAttribute("notices", noticeService.findActiveBySociety(user.getSociety().getId()));
        return "resident/notices";
    }
}
