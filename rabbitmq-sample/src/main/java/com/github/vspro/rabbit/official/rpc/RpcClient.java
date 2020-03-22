package com.github.vspro.rabbit.official.rpc;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeoutException;

public class RpcClient {


    private Connection connection;
    private Channel channel;

    public RpcClient() throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);
        connection = factory.newConnection();
        channel = connection.createChannel();
    }


    public String invoke(String message) throws IOException, InterruptedException {
        String correlationId = UUID.randomUUID().toString();

        String replyQueue = channel.queueDeclare().getQueue();
        AMQP.BasicProperties properties = new AMQP.BasicProperties
                .Builder()
                .correlationId(correlationId)
                .replyTo(replyQueue)
                .build();
        channel.basicPublish("", Constants.RPC.QUEUE_NAME, properties, message.getBytes());

        //处理调用结果
        BlockingQueue<String> blockingQueue = new ArrayBlockingQueue<String>(1);
        String tag = channel.basicConsume(replyQueue, true, (consumerTag, msg) -> {

            if (msg.getProperties().getCorrelationId().equals(correlationId)) {
                blockingQueue.offer(new String(msg.getBody(), "UTF-8"));
            }
        }, consumerTag -> {
        });

        String result = blockingQueue.take();
        channel.basicCancel(tag);
        return result;
    }


    public void close() throws IOException {
        connection.close();
    }


    public static void main(String[] args) throws IOException, TimeoutException {
        RpcClient rpcClient = new RpcClient();
        Scanner scanner = new Scanner(System.in);
        System.out.print("input message you want to sent(and 'bye' exits): ");
        while (scanner.hasNextLine()) {
            String msg = scanner.nextLine();
            if (msg.equals("bye")) {
                break;
            }
            String result = null;
            try {
                result = rpcClient.invoke(msg);
            } catch (InterruptedException e) {
                System.out.println("producer rpc invoke error !!");
            }
            System.out.println("producer recv rpc result [x]: " + result);
            System.out.print("input message you want to sent(and 'bye' exits): ");
        }
        rpcClient.close();
    }
}
