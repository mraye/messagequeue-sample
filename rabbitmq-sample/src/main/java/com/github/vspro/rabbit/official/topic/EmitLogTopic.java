package com.github.vspro.rabbit.official.topic;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 * routing - pattern
 * 使用 direct exchange
 */
public class EmitLogTopic {


    public static void main(String[] args) throws IOException, TimeoutException {


        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();

            channel.exchangeDeclare(Constants.Topic.EXCHANGE_NAME, BuiltinExchangeType.TOPIC);
            System.out.print("input route key: ");
            Scanner scanner = new Scanner(System.in);
            String routeKey = scanner.nextLine();

            System.out.print("input message you want to sent(and 'bye' exits): ");
            while (scanner.hasNextLine()){
                String msg = scanner.nextLine();
                if (msg.equals("bye")){
                    break;
                }
                channel.basicPublish(Constants.Topic.EXCHANGE_NAME, routeKey, null, msg.getBytes());
                System.out.println("producer sent [x]: "+ msg);
                System.out.print("input message you want to sent(and 'bye' exits): ");
            }

        }

    }
}
