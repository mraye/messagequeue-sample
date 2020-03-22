package com.github.vspro.rabbit.official.header;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;


/**
 * route pattern
 *
 * 使用 Headers 类型的交换器
 */
public class EmitLogHeader {


    public static void main(String[] args) throws IOException, TimeoutException {


        System.out.print("input headers number: ");
        Scanner scanner = new Scanner(System.in);
        int count = Integer.parseInt(scanner.nextLine());

        Map<String, Object> headers = new HashMap<>();
        int i = 0;
        while (true) {
            if (i==count){
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

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.exchangeDeclare(Constants.Headers.EXCHANGE_NAME, BuiltinExchangeType.HEADERS);

            AMQP.BasicProperties prop = new AMQP.BasicProperties.Builder()
                    .deliveryMode(MessageProperties.MINIMAL_PERSISTENT_BASIC.getDeliveryMode())
                    .priority(MessageProperties.PERSISTENT_TEXT_PLAIN.getPriority())
                    .headers(headers).build();
            System.out.print("input message you want to sent(and 'bye' exits): ");
            while (scanner.hasNextLine()) {
                String msg = scanner.nextLine();
                if (msg.equals("bye")) {
                    break;
                }

                //虽然API需要指定route_key,但是对于 Headers 类型，是会忽略route_key
                channel.basicPublish(Constants.Headers.EXCHANGE_NAME, Constants.Headers.ROUTE_KEY, prop, msg.getBytes());
                System.out.println("producer sent [x]: " + msg);
                System.out.print("input message you want to sent(and 'bye' exits): ");
            }
        }


    }
}
