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
import java.net.URISyntaxException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeoutException;

import office.drive.android.config.PropertyConfig;

/**
 * Created by NPOST on 2017-06-16.
 */

public class MessageService extends Service implements Runnable {

    private String RABBITMQ_URI;
    private String EXCHANGE_NAME;
    private String BINDING_KEY;

    Thread thread;
    boolean isRun = true;

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
        RABBITMQ_URI = PropertyConfig.getConfigValue(this, "rabbitmq.uri");
        EXCHANGE_NAME = PropertyConfig.getConfigValue(this, "rabbitmq.exchange");
        BINDING_KEY = PropertyConfig.getConfigValue(this, "rabbitmq.bindingkey");

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
            factory.setUri(RABBITMQ_URI); //local 내부접속
            Connection connection = factory.newConnection();
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(EXCHANGE_NAME, BuiltinExchangeType.DIRECT);
            String queueName = channel.queueDeclare().getQueue();

            channel.queueBind(queueName, EXCHANGE_NAME, BINDING_KEY);

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
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
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
