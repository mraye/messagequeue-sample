package com.github.vspro.rabbit.official.header;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

public class ReceiveLogHeader {


    public static void main(String[] args) throws IOException, TimeoutException {
        System.out.print("input headers number: ");
        Scanner scanner = new Scanner(System.in);
        int count = Integer.parseInt(scanner.nextLine());

        Map<String, Object> headers = new HashMap<>();
        int i = 0;
        while (true) {
            if (i == count) {
                break;
            }
            System.out.print("input headers key and value, separated by space: ");
            String line = scanner.nextLine();
            String[] strings = line.split(" ");
            headers.put(strings[0], strings[1]);
            i++;
        }


        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();

        String queueName = channel.queueDeclare(Constants.Headers.QUEUE_NAME, false, false, false, null).getQueue();
        channel.queueBind(queueName, Constants.Headers.EXCHANGE_NAME, Constants.Headers.BINDING_KEY, headers);


        System.out.println("consumer [*] Waiting for messages. To exit press CTRL+C");


        DeliverCallback callback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), "UTF-8");
            System.out.println("consumer received [" + message.getEnvelope().getDeliveryTag() + "]: " + msg);
        };


        channel.basicConsume(queueName, true, callback, consumerTag -> {
        });

    }
}
