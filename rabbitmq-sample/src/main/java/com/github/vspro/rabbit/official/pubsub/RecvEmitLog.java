package com.github.vspro.rabbit.official.pubsub;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class RecvEmitLog {


    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.exchangeDeclare(Constants.PubSub.EXCHANGE_NAME, BuiltinExchangeType.FANOUT);
        String queueName = channel.queueDeclare().getQueue();
        channel.queueBind(queueName, Constants.PubSub.EXCHANGE_NAME, "");


        System.out.println("consumer " + queueName + " [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback callback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), "UTF-8");
            System.out.println("consumer recv [x]: " + msg);
        };
        channel.basicConsume(queueName, true, callback, consumerTag -> { });

    }
}
