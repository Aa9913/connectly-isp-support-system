package com.isp.support.config;

import com.isp.support.service.*;
import com.isp.support.model.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

@ControllerAdvice
public class GlobalModel {
    @Value("${app.demo-seed:false}")
    private boolean demoSeed;
    private final CurrentUserService users;
    private final NotificationService notifications;

    public GlobalModel(CurrentUserService users, NotificationService notifications) {
        this.users = users;
        this.notifications = notifications;
    }

    @ModelAttribute void addShared(Model model) {
        var me = users.current();
        model.addAttribute("me", me);
        model.addAttribute("demoSeed", demoSeed);
        if (me != null) model.addAttribute("unreadCount", notifications.unreadCount(me));
        model.addAttribute("allStatuses", TicketStatus.values());
        model.addAttribute("allCategories", Category.values());
    }
}
