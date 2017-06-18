package office.drive.android.config;

import android.content.Context;

import org.springframework.http.HttpAuthentication;
import org.springframework.http.HttpBasicAuthentication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

import office.drive.android.domain.Inbox;

/**
 * Created by NPOST on 2017-06-17.
 */
public class AuthHeaders {

    /**
     * basic approach as of use the HttpEntity and resttemplate.exchange method (not recommended)
     * @return
     */
    public static HttpHeaders getHttpRequest(Context context) {

        String restuser = PropertyConfig.getConfigValue(context, "rest.user");
        String restpassword = PropertyConfig.getConfigValue(context, "rest.password");

        HttpAuthentication authHeader = new HttpBasicAuthentication(restuser, restpassword);
        HttpHeaders requestHeaders = new HttpHeaders();
        requestHeaders.setAuthorization(authHeader);
        requestHeaders.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        return requestHeaders;
    }

}
