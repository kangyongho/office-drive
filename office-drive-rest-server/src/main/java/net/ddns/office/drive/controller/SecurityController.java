package net.ddns.office.drive.controller;

import net.ddns.office.drive.config.PropertyConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

/**
 * Created by NPOST on 2017-06-07.
 */
@RestController
public class SecurityController {

    @GetMapping(value = "/encrypt/{word}")
    public String encode(@PathVariable("word") String word, BCryptPasswordEncoder encoder) {
        String encryption = encoder.encode(word);
        return encryption;
    }
}
