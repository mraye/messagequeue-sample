package com.github.vspro.rabbit.official.workqueues;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.Scanner;
import java.util.concurrent.TimeoutException;

/**
 *
 * work queues pattern
 * 先运行多个worker,后运行taskPublisher
 */
public class TaskPublisher {


    public static void main(String[] args) throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);

        try (Connection connection = factory.newConnection()) {

            Channel channel = connection.createChannel();
            channel.queueDeclare(Constants.WorkQueue.QUEUE_NAME, false, false, false, null);

            Scanner scanner = new Scanner(System.in);
            System.out.print("input message you want to sent(and 'bye' exits): ");
            while (scanner.hasNextLine()){
                String msg = scanner.nextLine();
                if (msg.equals("bye")){
                    break;
                }
                channel.basicPublish("", Constants.WorkQueue.QUEUE_NAME, null, msg.getBytes());
                System.out.println("producer sent [x]: "+ msg);
                System.out.print("input message you want to sent(and 'bye' exits): ");
            }

        }
    }
}
