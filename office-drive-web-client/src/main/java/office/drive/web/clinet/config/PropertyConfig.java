package office.drive.web.clinet.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * Created by NPOST on 2017-06-07.
 */
//@Component
@Configuration
@ConfigurationProperties(prefix = "config")
public class PropertyConfig {

    private String servername;
    private String info;
    private String user;
    private String password;
    private String accessmessage;

    public String getServername() {
        return servername;
    }

    public void setServername(String servername) {
        this.servername = servername;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAccessmessage() {
        return accessmessage;
    }

    public void setAccessmessage(String accessmessage) {
        this.accessmessage = accessmessage;
    }

}
