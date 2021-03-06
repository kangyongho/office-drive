package net.ddns.office.drive.helper;

import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Created by NPOST on 2017-06-14.
 * RabbitMQ Custom Helper class
 * apply new factory, connection
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
     * @param localhost
     */
    public RabbitMQHelper(String localhost) {
        this.factory = new ConnectionFactory();
        factory.setHost(localhost);
        this.host = localhost;
    }

    public RabbitMQHelper(String host, String username, String password) {
        factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setHost(host);
        factory.setPort(5672);
        this.host = host;
    }

    public RabbitMQHelper(String host, String username, String password, String virtualhost) {
        factory = new ConnectionFactory();
        factory.setUsername(username);
        factory.setPassword(password);
        factory.setHost(host);
        factory.setVirtualHost(virtualhost);
        factory.setPort(5672);
        this.host = host;
    }

    /**
     * @return Connection object
     * @throws IOException
     * @throws TimeoutException
     */
    private Connection getConnection() throws IOException, TimeoutException {
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
     * notice! do not use message.getBytes() method
     * instead use this, message.getBytes("UTF-8") method
     * @param message
     * @param bindingKey
     * @throws IOException
     */
    public void basicPublish(String message, String bindingKey) throws IOException {
        if (connection.isOpen()) {
            channel.basicPublish(exchangeName, bindingKey, null, message.getBytes("UTF-8"));
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
