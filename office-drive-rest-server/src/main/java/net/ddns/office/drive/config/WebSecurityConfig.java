package net.ddns.office.drive.config;

import net.ddns.office.drive.service.UserRepositoryUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.OAuth2ClientContext;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableOAuth2Client;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.web.filter.CharacterEncodingFilter;

import javax.sql.DataSource;

/**
 * Created by NPOST on 2017-06-07.
 */
@Configuration
@EnableWebSecurity
@EnableOAuth2Client
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @SuppressWarnings("SpringJavaAutowiringInspection") //Intellij bug message out
    @Autowired
    private DataSource dataSource;

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    OAuth2ClientContext oAuth2ClientContext;

    @Bean
    public RoleHierarchyImpl roleHierarchy() {
        RoleHierarchyImpl roleHierarchy = new RoleHierarchyImpl();
        roleHierarchy.setHierarchy("ROLE_ADMIN > ROLE_STAFF ROLE_STAFF > ROLE_USER ROLE_USER > ROLE_GUEST");
        return roleHierarchy;
    }

    @Configuration
    @Order(1)
    public static class AndroidConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/android/**")
                    .csrf().disable()
                    .httpBasic();
        }
    }

    @Configuration
    @Order(2)
    public static class WebConfiguration extends WebSecurityConfigurerAdapter {
        @Override
        protected void configure(HttpSecurity http) throws Exception {
            http
                    .antMatcher("/**")
                    .authorizeRequests()
                    .antMatchers("/", "/css/**", "/js/**").permitAll()
                    .antMatchers("/find/admin/**").hasRole("ADMIN")    //Role 별로 접근 권한을 준다.
                    .antMatchers("/find/staff/**").hasRole("STAFF")
                    .antMatchers("/find/user/**").hasRole("USER")      //ADMIN 로그인시 USER 권한도 갖고 있으므로 호출할 수 있다. (RoleHierarchyImpl 구현때문)
                    .anyRequest().authenticated()                      //포괄적인 설정을 나중에 한다.
                    .and()
                    .httpBasic();
        }
    }

    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth, UserRepositoryUserDetailsService uds) throws Exception {
        auth
                .authenticationProvider(authenticationProvider(uds)); //provider 제공시 jdbc authentication은 사용하지 않는다. UserDetailsService에서 이미 User Entity를 조회하기 때문
    }

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserRepositoryUserDetailsService uds) {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(uds);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

}
