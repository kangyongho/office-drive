package net.ddns.office.drive.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by NPOST on 2017-06-07.
 */
@Component
@ConfigurationProperties(prefix = "config")
public class PropertyConfig {

    //rest server properties
    private Map<String, String> rest = new HashMap<>();

    //RabbitMQ server properties
    private Map<String, String> rabbitmq = new HashMap<>();

    public Map<String, String> getRest() {
        return rest;
    }

    public Map<String, String> getRabbitmq() {
        return rabbitmq;
    }
}
