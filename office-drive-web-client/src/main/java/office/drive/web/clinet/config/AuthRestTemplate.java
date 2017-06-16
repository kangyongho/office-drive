package office.drive.web.clinet.config;

import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

/**
 * Created by NPOST on 2017-06-16.
 * @ConfigurationProperties 로 등록한 meta data 빈을 생성자에서 먼저 사용한다.
 */
@Service
public class AuthRestTemplate {

    private final RestTemplate restTemplate;

    @Autowired
    public AuthRestTemplate(RestTemplateBuilder builder, PropertyConfig propertySource) {
        this.restTemplate = builder.basicAuthorization(propertySource.getUser(), propertySource.getPassword()).build();
    }

    public Map getDashboard(String username) {
        return this.restTemplate.getForObject("http://localhost:8010/dashboard/{username}", Map.class, username);
    }

    public Map getDashboard(String username, Integer setpage) {
        return this.restTemplate.getForObject("http://localhost:8010/dashboard/{username}/{setpage}", Map.class, username, setpage);
    }


    /**
     * basic approach as of use the HttpEntity and resttemplate.exchange method (not recommended)
     * @return
     */
    public HttpEntity<String> getHttpRequest() {
        String plainCreds = "username" + ":" + "password"; //call variables from other source
        byte[] plainCredsBytes = plainCreds.getBytes();
        byte[] base64CredsBytes = Base64.encodeBase64(plainCredsBytes);
        String base64Creds = new String(base64CredsBytes);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Basic " + base64Creds);

        HttpEntity<String> request = new HttpEntity<String>(headers);
        return request;
    }
}
