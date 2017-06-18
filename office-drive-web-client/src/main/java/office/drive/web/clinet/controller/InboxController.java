package office.drive.web.clinet.controller;

import com.rabbitmq.client.BuiltinExchangeType;
import office.drive.web.clinet.config.PropertyConfig;
import office.drive.web.clinet.domain.Inbox;
import office.drive.web.clinet.domain.User;
import office.drive.web.clinet.helper.RabbitMQHelper;
import office.drive.web.clinet.security.CurrentUser;
import office.drive.web.clinet.service.InboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.TimeoutException;

/**
 * Created by NPOST on 2017-06-12.
 */
@Controller
public class InboxController {

    @Autowired private InboxService inboxService;
    @Autowired private PropertyConfig propertyConfig;

    @RequestMapping(value = "/inbox", method = RequestMethod.GET)
    public String inbox(@AuthenticationPrincipal Authentication auth, @CurrentUser User user, Model model) {
        boolean isAuthenticated = auth.isAuthenticated();
        String name = user.getName();

        Map<String, ?> inboxList = inboxService.getSentMessage(1, user);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("name", name);

        model.addAttribute("inboxList", inboxList.get("list"));
        model.addAttribute("startPage", inboxList.get("startPage"));

        model.addAttribute("inbox", new Inbox());
        return "send-message";
    }

    @RequestMapping(value = "/inbox/{setpage}", method = RequestMethod.GET)
    public String inboxPaging(@PathVariable("setpage") Integer setpage, @AuthenticationPrincipal Authentication auth, @CurrentUser User user, Model model) {
        boolean isAuthenticated = auth.isAuthenticated();
        String name = user.getName();

        Map<String, ?> inboxList = inboxService.getSentMessage(setpage, user);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("name", name);

        model.addAttribute("inboxList", inboxList.get("list"));
        model.addAttribute("startPage", inboxList.get("startPage"));

        model.addAttribute("inbox", new Inbox());
        return "send-message";
    }

    @PostMapping(value = "/inbox/send")
    public String insertMessage(@ModelAttribute Inbox inboxForm, @CurrentUser User user) {
        Inbox inbox = new Inbox();
        inbox.setMessage(inboxForm.getMessage());
        inbox.setTitle(inboxForm.getTitle());
        inbox.setReceiver(inboxForm.getReceiver());

        inbox.setSender(user.getName());

        inbox.setDate(LocalDateTime.now());

        inboxService.insert(inbox);

        return "redirect:/inbox";
    }

    @GetMapping(value = "/rabbitmq")
    public String inboxRabbitMQ(@AuthenticationPrincipal Authentication auth, @CurrentUser User user, Model model) {
        boolean isAuthenticated = auth.isAuthenticated();
        String name = user.getName();

        Map<String, ?> inboxList = inboxService.getSentMessage(1, user);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("name", name);

        model.addAttribute("inboxList", inboxList.get("list"));
        model.addAttribute("startPage", inboxList.get("startPage"));

        model.addAttribute("inbox", new Inbox());
        return "send-message-rabbitmq";
    }

    @GetMapping(value = "/rabbitmq/{setpage}")
    public String rabbitmqPaging(@PathVariable("setpage") Integer setpage, @AuthenticationPrincipal Authentication auth, @CurrentUser User user, Model model) {
        boolean isAuthenticated = auth.isAuthenticated();
        String name = user.getName();

        Map<String, ?> inboxList = inboxService.getSentMessage(setpage, user);

        model.addAttribute("isAuthenticated", isAuthenticated);
        model.addAttribute("name", name);

        model.addAttribute("inboxList", inboxList.get("list"));
        model.addAttribute("startPage", inboxList.get("startPage"));

        model.addAttribute("inbox", new Inbox());
        return "send-message-rabbitmq";
    }

    @PostMapping(value = "/rabbitmq/send")
    public String sendMessageWithRabbitMQ(@ModelAttribute Inbox inboxForm) throws IOException, TimeoutException {
        //RabbitMQ AMQP로 안드로이드에게 PUSH 메시지 전송
        RabbitMQHelper rabbitMQHelper = new RabbitMQHelper(
                propertyConfig.getRabbitmq().get("host"),
                propertyConfig.getRabbitmq().get("username"),
                propertyConfig.getRabbitmq().get("password"),
                "pushService");  //helper object 생성

        rabbitMQHelper.getChannel("inbox", BuiltinExchangeType.DIRECT);   //변화하는 부분, Exchange name, Type

        String message = inboxForm.getMessage();
        String bindingKey = "broadcast";
        rabbitMQHelper.basicPublish(message, bindingKey);                 //변화하는부분, binding key (receiver)

        rabbitMQHelper.closeConnection();

        return "redirect:/rabbitmq";
    }

}
