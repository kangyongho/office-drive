package office.drive.android;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Consumer;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

import office.drive.android.config.PropertyConfig;

/**
 * Created by NPOST on 2017-06-16.
 */

public class MessageService extends Service implements Runnable {

    private String RABBITMQ_USER;
    private String RABBITMQ_PASSWORD;
    private String RABBITMQ_HOST;
    private String RABBITMQ_VIRTUAL_HOST;
    private String RABBITMQ_EXCHANGE_NAME;
    private String RABBITMQ_BINDING_KEY;

    Thread thread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(this.getClass().getName(), "MessageService 실행");

        //set RabbitMQ properties
        RABBITMQ_USER = PropertyConfig.getConfigValue(this, "rabbitmq.user");
        RABBITMQ_PASSWORD = PropertyConfig.getConfigValue(this, "rabbitmq.password");
        RABBITMQ_HOST = PropertyConfig.getConfigValue(this, "rabbitmq.host");
        RABBITMQ_VIRTUAL_HOST = PropertyConfig.getConfigValue(this, "rabbitmq.virtualhost");
        RABBITMQ_EXCHANGE_NAME = PropertyConfig.getConfigValue(this, "rabbitmq.exchange");
        RABBITMQ_BINDING_KEY = PropertyConfig.getConfigValue(this, "rabbitmq.bindingkey");

        thread = new Thread(this);
        thread.start();
    }

    @Override
    public void onDestroy() {
        super.onDestroy(); //TODO. 서비스관리로직 구현
    }

    @Override
    public void run() {
        receiveRabbitMQ(); //TODO. 연결상태감지 재연결 구현
    }


    public void receiveRabbitMQ() {
        ConnectionFactory factory = new ConnectionFactory();

        try {
            factory.setUsername(RABBITMQ_USER);
            factory.setPassword(RABBITMQ_PASSWORD);
            factory.setHost(RABBITMQ_HOST);
            factory.setVirtualHost(RABBITMQ_VIRTUAL_HOST);
            factory.setPort(5672);  //production 환경에서는 Nginx port forwarding 때문에 생략

            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(RABBITMQ_EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            String queueName = channel.queueDeclare().getQueue();

            channel.queueBind(queueName, RABBITMQ_EXCHANGE_NAME, RABBITMQ_BINDING_KEY);

            Log.i(this.getClass().getName(), "RabbitMQ 연결됨");

            Consumer consumer = new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    String message = new String(body, "UTF-8");
                    Log.i(this.getClass().getName(), "Received : " + message + " " + envelope.getRoutingKey());
                    getNotification(message);
                }
            };
            channel.basicConsume(queueName, true, consumer);

        } catch (IOException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }
    }

    private void getNotification(String message) throws UnsupportedEncodingException {

        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        Log.i("MSG ", message);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setContentTitle("RabbitMQ")
                .setContentText(message) //전면부 알림창 메시지
                .setSmallIcon(R.drawable.ic_face_white_24dp)
                .setSound(alarmSound)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(
                        this,
                        0,
                        new Intent(getApplicationContext(), MainActivity.class).putExtra("message", message),
                        PendingIntent.FLAG_UPDATE_CURRENT));

        int notiId = 002;
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        manager.notify(notiId, builder.build());
    }
}
