package office.drive.web.clinet.controller;

import office.drive.web.clinet.config.PropertyConfig;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.annotation.Resource;

/**
 * Created by NPOST on 2017-06-07.
 */
@Controller
public class CloudConfigController {

    @Resource private PropertyConfig propertyConfig;

    @RequestMapping(value = "/check", method = RequestMethod.GET)
    @ResponseBody
    public PropertyConfig check() {
        return propertyConfig;
    }

}
