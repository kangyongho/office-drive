package office.drive.web.clinet.helper;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by NPOST on 2017-06-14.
 * RabbiMQ Custom Helper class
 * use new factory, connection
 */
public class RabbitMQHelper {

    private ConnectionFactory factory;
    private Connection connection;
    private String host;
    private Channel channel;
    private String exchangeName;

    public RabbitMQHelper() {
    }

    /**
     * for both user as publisher, consumer
     * @param host
     */
    public RabbitMQHelper(String host) {
        this.factory = new ConnectionFactory();
        factory.setHost(host);
        this.host = host;
    }

    /**
     * for both user as publisher, consumer
     * @return Connection object
     * @throws IOException
     * @throws TimeoutException
     */
    public Connection getConnection() throws IOException, TimeoutException {
        if (this.connection == null) {
            this.connection = factory.newConnection();
            return this.connection;
        }
        else {
            return this.connection;
        }
    }

    /**
     * for both user as publisher, consumer
     * return only Channel object
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    public Channel getChannel() throws IOException, TimeoutException {
        if (this.connection == null) {
            getConnection();
            channel = connection.createChannel();
            return channel;
        }
        else {
            return channel;
        }
    }

    /**
     * for both user as publisher, consumer
     * return Channel object applied with ExchangeName and ExchangeType
     * @param exchangeName
     * @param type
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    public Channel getChannel(String exchangeName, BuiltinExchangeType type) throws IOException, TimeoutException {
        if (this.connection == null) {
            this.exchangeName = exchangeName;
            getConnection();
            channel = connection.createChannel();
            channel.exchangeDeclare(exchangeName, type);
            return channel;
        }
        else {
            channel.exchangeDeclare(exchangeName, type);
            return channel;
        }
    }

    /**
     * for both user as publisher, consumer
     * set ExchangeDeclare method
     * @param exchangeName
     * @param type
     * @throws IOException
     */
    public void setExchangeDeclare(String exchangeName, BuiltinExchangeType type) throws IOException {
        if (channel.isOpen()) {
            channel.exchangeDeclare(exchangeName, type);
        }
        else {
            throw new IOException();
        }
    }

    /**
     * only for user as publisher
     * @param message
     * @param bindingKey
     * @throws IOException
     */
    public void basicPublish(String message, String bindingKey) throws IOException {
        if (connection.isOpen()) {
            channel.basicPublish(exchangeName, bindingKey, null, message.getBytes());
        }
        else {
            throw new IOException();
        }
    }

    /**
     * only for user as publisher
     * @return queue name
     * @throws IOException
     */
    public String createQueueName() throws IOException {
        if (channel.isOpen()) {
            return channel.queueDeclare().getQueue();
        }
        else {
            throw new IOException();
        }
    }

    /**
     * only for user as consumer
     * @param queueName
     * @param callback
     * @throws IOException
     */
    public void basicConsume(String queueName, Consumer callback) throws IOException {
        if (channel.isOpen()) {
            channel.basicConsume(queueName, true, callback);
        }
        else {
            throw new IOException();
        }
    }

    /**
     * only for user as publisher
     * consumer keep connection, it depend on your need.
     * close channel and connection
     * @throws IOException
     */
    public void closeConnection() throws IOException, TimeoutException {
        if (connection.isOpen()) {
            channel.close();
            connection.close();
        }
        else {
            throw new IOException();
        }
    }

    /**
     * for both user as publisher, consumer
     * @return host
     */
    public String getHost() {
        return host;
    }

    /**
     * for both user as publisher, consumer
     * @return
     */
    public String getExchangeName() {
        return exchangeName;
    }
}
