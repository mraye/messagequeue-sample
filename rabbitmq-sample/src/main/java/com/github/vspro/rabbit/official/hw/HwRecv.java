package com.github.vspro.rabbit.official.hw;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class HwRecv {


    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);

        try (Connection connection = factory.newConnection()) {

            Channel channel = connection.createChannel();
            channel.queueDeclare(Constants.Hw.QUEUE_NAME, false, false, false, null);
            System.out.println(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback callback = ((consumerTag, message) -> {
                String msg = new String(message.getBody(), "UTF-8");
                System.out.println("consumer recv [x]: " + msg);
            });
            channel.basicConsume(Constants.Hw.QUEUE_NAME, true, callback, (consumerTag -> {}));
        }

    }
}
