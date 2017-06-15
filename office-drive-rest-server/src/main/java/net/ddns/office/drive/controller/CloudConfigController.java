package net.ddns.office.drive.controller;

import net.ddns.office.drive.domain.PropertyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by NPOST on 2017-06-07.
 */
@Controller
@EnableConfigurationProperties(PropertyConfig.class)
@RefreshScope
public class CloudConfigController {

    @Autowired
    private PropertyConfig propertyConfig;

    @RequestMapping(value = "/check", method = RequestMethod.GET) @ResponseBody
    public PropertyConfig check() {
        return propertyConfig;
    }
}
