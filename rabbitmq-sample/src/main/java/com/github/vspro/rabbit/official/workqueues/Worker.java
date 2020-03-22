package com.github.vspro.rabbit.official.workqueues;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DeliverCallback;

import java.util.concurrent.TimeUnit;

public class Worker {


    public static void main(String[] args) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);

        Connection connection = factory.newConnection();
        Channel channel = connection.createChannel();
        channel.queueDeclare(Constants.WorkQueue.QUEUE_NAME, false, false, false, null);

        String workerName = channel.getChannelNumber() + "";
        System.out.println("worker: " + workerName + " [*] Waiting for messages. To exit press CTRL+C");

        DeliverCallback callback = (consumerTag, message) -> {
            String msg = new String(message.getBody(), "UTF-8");
            System.out.println("consumer recv [x]: " + msg);
            try {
                try {
                    doWork(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } finally {
                System.out.println("worker [x] Done...");
            }
        };

        channel.basicConsume(Constants.WorkQueue.QUEUE_NAME, true, callback, consumerTag -> {
        });

    }

    private static void doWork(String msg) throws Exception {

        for (char c : msg.toCharArray()) {
            if (c == '.') {
                TimeUnit.SECONDS.sleep(1);
            }
        }
    }
}
