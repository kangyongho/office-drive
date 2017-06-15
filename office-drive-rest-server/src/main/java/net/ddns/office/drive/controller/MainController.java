package net.ddns.office.drive.controller;

import net.ddns.office.drive.domain.User;
import net.ddns.office.drive.security.CurrentUser;
import net.ddns.office.drive.service.InboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Map;

/**
 * Created by NPOST on 2017-06-11.
 */
@Controller
public class MainController {

    @Autowired
    InboxService inboxService;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(@AuthenticationPrincipal Authentication auth, @CurrentUser User user, Model model) {
        boolean isAuthenticated = auth.isAuthenticated();
        String name = user.getName();
//        List<Inbox> inboxList = inboxService.getListAll();

        Map<String, ?> inboxList = inboxService.getReceivedMessage(1, user);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("name", name);
//        model.addAttribute("inboxList", inboxList);

        model.addAttribute("inboxList", inboxList.get("list"));
//        model.addAttribute("startPage", inboxList.get("startPage"));
        model.addAttribute("startPage", 1);
        return "dashboard";
    }

    @RequestMapping(value = "/dashboard/{setpage}", method = RequestMethod.GET)
    public String dashboardPaging(@PathVariable("setpage") Integer setpage, @AuthenticationPrincipal Authentication auth, @CurrentUser User user, Model model) {
        boolean isAuthenticated = auth.isAuthenticated();
        String name = user.getName();
//        List<Inbox> inboxList = inboxService.getListAll();

        Map<String, ?> inboxList = inboxService.getReceivedMessage(setpage, user);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("name", name);
//        model.addAttribute("inboxList", inboxList);

        model.addAttribute("inboxList", inboxList.get("list"));
        model.addAttribute("startPage", inboxList.get("startPage"));
        return "dashboard";
    }


    @RequestMapping(value = "/dexample", method = RequestMethod.GET)
    public String dashboardexample(Model model) {
        return "example/dashboard-example";
    }
}
