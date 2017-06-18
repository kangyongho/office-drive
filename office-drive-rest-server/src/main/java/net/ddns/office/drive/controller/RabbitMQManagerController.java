package net.ddns.office.drive.controller;

import com.rabbitmq.client.BuiltinExchangeType;
import net.ddns.office.drive.config.PropertyConfig;
import net.ddns.office.drive.domain.Inbox;
import net.ddns.office.drive.helper.RabbitMQHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.TimeoutException;

/**
 * Created by NPOST on 2017-06-17.
 */
@RestController
public class RabbitMQManagerController {

    @Autowired PropertyConfig propertyConfig;

    @PostMapping(value = "/android/rabbitmq/send")
    public void sendMessageWithRabbitMQ(@RequestBody Inbox inbox) throws IOException, TimeoutException {

        inbox.setDate(LocalDateTime.now());    //날짜 변환의 문제로 서버시간으로 저장.

        //RabbitMQ AMQP로 안드로이드에게 PUSH 메시지 전송
        RabbitMQHelper rabbitMQHelper = new RabbitMQHelper(
                propertyConfig.getRabbitmq().get("host"),
                propertyConfig.getRabbitmq().get("username"),
                propertyConfig.getRabbitmq().get("password"),
                "pushService");  //helper object 생성

        rabbitMQHelper.getChannel("inbox", BuiltinExchangeType.DIRECT);   //변화하는 부분, Exchange name, Type

        String message = inbox.getMessage();
        String bindingKey = "broadcast";
        rabbitMQHelper.basicPublish(message, bindingKey);                 //변화하는부분, binding key (receiver)

        rabbitMQHelper.closeConnection();
    }

    @GetMapping(value = "/android/rabbitmq/get")
    public void test() throws IOException, TimeoutException {

        //RabbitMQ AMQP로 안드로이드에게 PUSH 메시지 전송
        RabbitMQHelper rabbitMQHelper = new RabbitMQHelper(
                propertyConfig.getRabbitmq().get("host"),
                propertyConfig.getRabbitmq().get("username"),
                propertyConfig.getRabbitmq().get("password"),
                "pushService");  //helper object 생성

        rabbitMQHelper.getChannel("inbox", BuiltinExchangeType.DIRECT);   //변화하는 부분, Exchange name, Type

        String message = "테스트 메시지";
        String bindingKey = "broadcast";
        rabbitMQHelper.basicPublish(message, bindingKey);                 //변화하는부분, binding key (receiver)

        rabbitMQHelper.closeConnection();
    }

}
