package office.drive.android;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;


import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;

import office.drive.android.config.AuthHeaders;
import office.drive.android.config.PropertyConfig;
import office.drive.android.domain.Inbox;

/**
 * Created by NPOST on 2017-06-16.
 */

public class SendMessageActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, Runnable {

    private Thread thread;

    private EditText title;
    private EditText message;
    private TextView showMessage;

    private String receiver;

    private String RABBITMQ_URI;
    private String RABBITMQ_REST_URI;
    private String EXCHANGE_NAME;
    private String BINDING_KEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.send_message);

        //find UI component
        title = (EditText) findViewById(R.id.title);
        message = (EditText) findViewById(R.id.message);
        showMessage = (TextView) findViewById(R.id.showMessage);

        //set RabbitMQ properties
        RABBITMQ_URI = PropertyConfig.getConfigValue(this, "rabbitmq.uri");
        RABBITMQ_REST_URI = PropertyConfig.getConfigValue(this, "rabbitmq.rest.uri");
        EXCHANGE_NAME = PropertyConfig.getConfigValue(this, "rabbitmq.exchange");
        BINDING_KEY = PropertyConfig.getConfigValue(this, "rabbitmq.bindingkey");

        //set spinner
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);

        List<String> categories = new ArrayList<String>();
        categories.add("daniel");
        categories.add("mark");
        categories.add("ted");
        categories.add("angela");
        categories.add("soy");
        categories.add("tom");

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categories);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(dataAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        String item = adapterView.getItemAtPosition(i).toString();
        showMessage.setText(item);
        receiver = item;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) { }

    //set action click button
    public void sendMessageRabbitMQ(View view) {
        //보낸 메시지 출력
        showMessage.setText(message.getText().toString());
        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void run() {
        sendRabbitMQ();
    }

    public void sendRabbitMQ() {
        //메시지 객체 생성
        Inbox inbox = new Inbox();
        inbox.setTitle(title.getText().toString());
        inbox.setMessage(message.getText().toString());
        inbox.setReceiver(receiver);
        inbox.setSender("android");

        //인증 헤더 생성
        HttpHeaders httpHeaders = AuthHeaders.getHttpRequest(this);

        //spring android RestTemplate Http 통신
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.getMessageConverters().add(new MappingJackson2HttpMessageConverter());

        try {
            restTemplate.exchange(RABBITMQ_REST_URI, HttpMethod.POST, new HttpEntity<Inbox>(inbox, httpHeaders), Inbox.class);
        } catch (HttpClientErrorException e) {
            Log.e("HTTP-Exception", e.getLocalizedMessage(), e);
        } catch (ResourceAccessException e) {
            Log.e("Resource Exception", e.getLocalizedMessage(), e);
        }
    }

    /**
     * Test Method
     * Java Encoding 문제
     * message.getBytes("UTF-8")로 사용해야 한다.
     * Android Java 에서도 이 형태로 사용하자.
     */
    public void sendRabbitMQSelf() {
        //메시지 객체 생성
        String messages = message.getText().toString();

        ConnectionFactory factory = new ConnectionFactory();

        try {
            factory.setUri(RABBITMQ_URI);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }
        Connection connection = null;
        try {
            connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);

            channel.basicPublish(EXCHANGE_NAME, BINDING_KEY, null, messages.getBytes("UTF-8"));

            channel.close();
            connection.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }
}
