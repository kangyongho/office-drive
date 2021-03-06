package office.drive.web.clinet.config;

import office.drive.web.clinet.service.UserRepositoryUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

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
                .formLogin()
                    .loginPage("/login")
                    .failureUrl("/login-error")
                    .successForwardUrl("/login-success")
                    .permitAll()
                    .and()
                .httpBasic()
                    .and()
                .csrf()
                    .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) //javascript로 post요청을 할 때 csrf token을 넣어준다. 확실하진 않다. <form> post 요청과 다르다.
                    .and()
                .headers()
                    .contentSecurityPolicy("default-src 'self'").reportOnly(); //크로스 도메인 정책, CORS 설정, 외부도메인 로딩 설정
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
