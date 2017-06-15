package net.ddns.office.drive.service;

import net.ddns.office.drive.domain.User;
import net.ddns.office.drive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by NPOST on 2017-06-07.
 */
@Service
public class UserService {

    private final UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public User findOne(String name) {
        return userRepository.findOne(name);
    }

    //    @PreAuthorize("hasRole('ADMIN')")
    public User findByUserName(String name) {
        return userRepository.findByUserName(name);
    }
}
