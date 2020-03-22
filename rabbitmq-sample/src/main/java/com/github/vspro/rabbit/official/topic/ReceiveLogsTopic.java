package com.github.vspro.rabbit.official.topic;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ReceiveLogsTopic {


    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        channel.exchangeDeclare(Constants.Topic.EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
        String queueName = channel.queueDeclare().getQueue();

        System.out.print("input binding keys, separated by space: ");
        Scanner scanner = new Scanner(System.in);
        String bindingKey = scanner.nextLine();
        for (String bdk: bindingKey.split(" ")){
            channel.queueBind(queueName, Constants.Topic.EXCHANGE_NAME, bdk);
        }

        System.out.println("consumer " + queueName + " [*] Waiting for messages. To exit press CTRL+C");
        DeliverCallback callback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), "UTF-8");
            System.out.println("consumer recv [x]: " + msg);
        };
        channel.basicConsume(queueName, true, callback, consumerTag -> { });

    }
}
