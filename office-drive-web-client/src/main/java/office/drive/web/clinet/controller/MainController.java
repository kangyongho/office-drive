package office.drive.web.clinet.controller;

import office.drive.web.clinet.domain.User;
import office.drive.web.clinet.security.CurrentUser;
import office.drive.web.clinet.config.AuthRestTemplate;
import office.drive.web.clinet.service.InboxService;
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

    @Autowired InboxService inboxService;
    @Autowired AuthRestTemplate authRestTemplate;

    @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
    public String dashboard(@AuthenticationPrincipal Authentication auth, @CurrentUser User user, Model model) {
        boolean isAuthenticated = auth.isAuthenticated();
        String name = user.getName();

        //Rest API authentication call
        Map map = authRestTemplate.getDashboard(name);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("name", name);

        model.addAttribute("inboxList", map.get("inboxList"));
        model.addAttribute("startPage", map.get("startPage"));

        return "dashboard";
    }


    @RequestMapping(value = "/dashboard/{setpage}", method = RequestMethod.GET)
    public String dashboardPaging(@PathVariable("setpage") Integer setpage, @AuthenticationPrincipal Authentication auth, @CurrentUser User user, Model model) {
        boolean isAuthenticated = auth.isAuthenticated();
        String name = user.getName();

        //Rest API authentication call
        Map map = authRestTemplate.getDashboard(name, setpage);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("name", name);

        model.addAttribute("inboxList", map.get("inboxList"));
        model.addAttribute("startPage", map.get("startPage"));

        return "dashboard";
    }

}
