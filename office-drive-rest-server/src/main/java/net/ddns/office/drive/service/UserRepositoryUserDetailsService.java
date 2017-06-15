package net.ddns.office.drive.service;

import net.ddns.office.drive.domain.User;
import net.ddns.office.drive.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Map;

/**
 * Created by NPOST on 2017-06-07.
 */
@Service
@Transactional
public class UserRepositoryUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Autowired
    public UserRepositoryUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /*
     * (non-Javadoc)
     *
     * @see org.springframework.security.core.userdetails.UserDetailsService#
     * loadUserByUsername(java.lang.String)
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Map<String, Object> userAndAuthority = userRepository.findAllRelatedEntity(username);
        User user = (User) userAndAuthority.get("user");
        if (user == null) {
            throw new UsernameNotFoundException("Could not find user " + username);
        }
        return new CustomUserDetails(user, userAndAuthority.get("authority").toString());
    }

    private final static class CustomUserDetails extends User implements UserDetails {
        private String authority;
        private CustomUserDetails(User user, String authority) {
            super(user);
            this.authority = authority;
        }

        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            return AuthorityUtils.createAuthorityList(authority);
        }

        @Override
        public String getUsername() {
            return getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
