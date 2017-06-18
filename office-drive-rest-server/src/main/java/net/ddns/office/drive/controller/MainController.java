package net.ddns.office.drive.controller;

import net.ddns.office.drive.service.InboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NPOST on 2017-06-11.
 */
@RestController
public class MainController {

    @Autowired
    InboxService inboxService;

    @RequestMapping(value = "/dashboard/{username}", method = RequestMethod.GET)
    public Map<String, Object> dashboard(@PathVariable("username") String username, Model model) {
        Map<String, Object> inboxList = inboxService.getReceivedMessageRestAPI(1, username);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("inboxList", inboxList.get("list"));
        dashboard.put("startPage", 1);

        return dashboard;
    }

    @RequestMapping(value = "/dashboard/{username}/{setpage}", method = RequestMethod.GET)
    public Map<String, Object> dashboardPaging(@PathVariable("setpage") Integer setpage, @PathVariable("username") String username, Model model) {
        Map<String, Object> inboxList = inboxService.getReceivedMessageRestAPI(setpage, username);

        Map<String, Object> dashboard = new HashMap<>();
        dashboard.put("inboxList", inboxList.get("list"));
        dashboard.put("startPage", inboxList.get("startPage"));

        return dashboard;
    }
}
