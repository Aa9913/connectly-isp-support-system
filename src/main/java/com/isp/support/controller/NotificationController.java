package com.isp.support.controller;

import com.isp.support.service.*;
import com.isp.support.model.Role;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class NotificationController {
    private final NotificationService notifications;
    private final CurrentUserService current;

    public NotificationController(NotificationService notifications, CurrentUserService current) {
        this.notifications = notifications;
        this.current = current;
    }

    @GetMapping("/notifications")
    public String index(Model model) {
        var me = current.require();
        model.addAttribute("notifications", me.role() == Role.ADMIN ? java.util.List.of() : notifications.forUser(me));
        return "notifications";
    }

    @PostMapping("/notifications/{id}/read")
    public String markRead(@PathVariable String id) {
        notifications.markRead(id, current.require());
        return "redirect:/notifications";
    }
}
