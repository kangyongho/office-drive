package net.ddns.office.drive.controller;

import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import net.ddns.office.drive.domain.Inbox;
import net.ddns.office.drive.helper.RabbitMQHelper;
import net.ddns.office.drive.service.InboxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

/**
 * Created by NPOST on 2017-06-17.
 */
@RestController
public class RabbitMQManagerController {

    @Autowired
    private InboxService inboxService;

    @PostMapping(value = "/android/rabbitmq/send")
    public void sendMessageWithRabbitMQ(@RequestBody Inbox inbox) throws IOException, TimeoutException {

        inbox.setDate(LocalDateTime.now());

        System.out.println(inbox.getTitle());
        System.out.println(inbox.getReceiver());
        System.out.println(inbox.getSender());
        System.out.println(inbox.getMessage());
        System.out.println(inbox.getDate());    //날짜 변환의 문제로 서버시간으로 저장.

        //메시지 DB 저장
//        inboxService.insert(inbox);

        //RabbitMQ AMQP로 안드로이드에게 PUSH 메시지 전송
        RabbitMQHelper rabbitMQHelper = new RabbitMQHelper("localhost"); //host 로 객체생성
        rabbitMQHelper.getChannel("inbox", BuiltinExchangeType.DIRECT);  //변화하는 부분, Exchange name, Type

        String message = inbox.getMessage();
        String bindingKey = "broadcast";
        rabbitMQHelper.basicPublish(message, bindingKey);                //변화하는부분, binding key (receiver)

        rabbitMQHelper.closeConnection();
    }

}
