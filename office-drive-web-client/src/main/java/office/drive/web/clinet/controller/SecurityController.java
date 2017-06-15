package office.drive.web.clinet.controller;

import office.drive.web.clinet.domain.User;
import office.drive.web.clinet.security.CurrentUser;
import office.drive.web.clinet.service.InboxService;
import office.drive.web.clinet.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by NPOST on 2017-06-07.
 */
@Controller
public class SecurityController {

    @Autowired
    UserService userService;
    @Autowired BCryptPasswordEncoder encoder;
    @Autowired
    InboxService inboxService;

    @GetMapping("/encrypt/{word}")
    @ResponseBody
    public String encode(@PathVariable("word") String word, BCryptPasswordEncoder encoder) {
        String encryption = encoder.encode(word);
        return encryption;
    }

    @GetMapping("/")
    public String index(@CurrentUser User user) {
        if (null == user) {
            return "index";
        }
        else {
            return "redirect:/dashboard";
        }
    }

    @GetMapping("/login")
    public String login(@CurrentUser User user) {
        if (null == user) {
            return "index";
        }
        else {
            return "redirect:/dashboard";
        }
    }

    @RequestMapping(value = "/login-error", method = RequestMethod.GET)
    public String loginError(Model model) {
        model.addAttribute("loginError", true);
        return "index";
    }

    @RequestMapping(value = "/login-success", method = RequestMethod.POST)
    public String dashboard(@AuthenticationPrincipal Authentication auth, @CurrentUser User currentUser, Model model) {
//        boolean isAuthenticated = auth.isAuthenticated();
//        String name = currentUser.getName();
//        List<Inbox> inboxList = inboxService.getListAll();
//
//        model.addAttribute("name", name);
//        model.addAttribute("isAuthenticated", isAuthenticated);
//        model.addAttribute("inboxList", inboxList);
        return "redirect:dashboard";
    }

    @RequestMapping(value = "/mylogout", method = RequestMethod.GET)
    public String logout(@AuthenticationPrincipal Authentication auth, Model model) {
        boolean bool = auth.isAuthenticated();
        model.addAttribute("isAuthenticated", bool);
        return "logout";
    }

    @RequestMapping(value = "/csp", method = RequestMethod.GET)
    public String logoutSuccess() { return "contentsecuritypolicy"; }



    @RequestMapping(value = "/find/admin/{name}", method = RequestMethod.GET)
    @ResponseBody
    public User findOne(@PathVariable("name") String name) { return userService.findOne(name); }

    @RequestMapping(value = "/find/staff/{name}", method = RequestMethod.GET)
    @ResponseBody
    public User findUser(@PathVariable("name") String name) { return userService.findOne(name); }

    @RequestMapping(value = "/find/user/{name}", method = RequestMethod.GET)
    @ResponseBody
    public User findUser2(@PathVariable("name") String name) { return userService.findOne(name); }

    @RequestMapping(value = "/search/{name}", method = RequestMethod.GET)
    @ResponseBody
    public User findByUserName(@PathVariable("name") String name, @CurrentUser User user) { return userService.findByUserName(name); }



    @GetMapping("/user") @ResponseBody
    public User defaultInfo(@CurrentUser User currentUser) { return currentUser; }

    @GetMapping("/facebook") @ResponseBody
    public Principal getPrincipal(Principal principal) {
        return principal;
    }
}
