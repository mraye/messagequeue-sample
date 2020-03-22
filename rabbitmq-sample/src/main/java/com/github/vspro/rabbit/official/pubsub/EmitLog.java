package com.github.vspro.rabbit.official.pubsub;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;


/**
 * publish/subscribe pattern
* 使用 fanout exchange 类型， route key 对于 fanout 类型设置无效
 */
public class EmitLog {


    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);

        try (Connection connection = factory.newConnection()) {

            Channel channel = connection.createChannel();
            channel.exchangeDeclare(Constants.PubSub.EXCHANGE_NAME, BuiltinExchangeType.FANOUT);

            Scanner scanner = new Scanner(System.in);
            System.out.print("input message you want to sent(and 'bye' exits): ");
            while (scanner.hasNextLine()){
                String msg = scanner.nextLine();
                if (msg.equals("bye")){
                    break;
                }
                channel.basicPublish(Constants.PubSub.EXCHANGE_NAME, "", null, msg.getBytes());
                System.out.println("producer sent [x]: "+ msg);
                System.out.print("input message you want to sent(and 'bye' exits): ");
            }

        }

    }
}
