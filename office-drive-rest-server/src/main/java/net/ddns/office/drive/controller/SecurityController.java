package net.ddns.office.drive.controller;

import net.ddns.office.drive.domain.User;
import net.ddns.office.drive.security.CurrentUser;
import net.ddns.office.drive.service.InboxService;
import net.ddns.office.drive.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

/**
 * Created by NPOST on 2017-06-07.
 */
@Controller
public class SecurityController {

    @Autowired UserService userService;
    @Autowired BCryptPasswordEncoder encoder;
    @Autowired InboxService inboxService;

    @GetMapping("/encrypt/{word}")
    @ResponseBody
    public String encode(@PathVariable("word") String word, BCryptPasswordEncoder encoder) {
        String encryption = encoder.encode(word);
        return encryption;
    }


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
