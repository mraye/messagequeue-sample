package com.github.vspro.rabbit.official.rpc;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.*;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.concurrent.TimeoutException;

public class RpcServer {


    private Connection connection;
    private Channel channel;
    private Object lock = new Object();

    public RpcServer() throws IOException, TimeoutException {

        ConnectionFactory  factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);

        connection = factory.newConnection();
        channel = connection.createChannel();

    }


    public void start() throws IOException {

        channel.queueDeclare(Constants.RPC.QUEUE_NAME, false, false, false, null);
        channel.queuePurge(Constants.RPC.QUEUE_NAME);
        channel.basicQos(1);
        System.out.println("server waiting for rpc requests [x]..");


        DeliverCallback callback = (consumerTag, message) -> {

            //构造响应队列
            AMQP.BasicProperties prop = new AMQP.BasicProperties.Builder()
                    .correlationId(message.getProperties().getCorrelationId())
                    .build();

            String response = "";
            String msg = new String(message.getBody(), "UTF-8");

            try {
                Integer num = Integer.parseInt(msg);
                response += fib(num);
            } catch (RuntimeException e) {
                System.out.println("server can not handle this type, echo origin message [x]..");
                response = "echo from "+ msg;
            } finally {

                channel.basicPublish("", message.getProperties().getReplyTo(),prop, response.getBytes());
                channel.basicAck(message.getEnvelope().getDeliveryTag(), false);

                synchronized (lock){
                    lock.notify();
                }
            }
        };

        channel.basicConsume(Constants.RPC.QUEUE_NAME, false, callback, consumerTag -> {});
        while (true){
            synchronized (lock){
                try {
                    lock.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }


    }

    private int fib(Integer n) {
        if (n ==0){
            return 0;
        }

        if (n ==1){
            return 1;
        }
        return fib(n-1)+ fib(n-2);
    }

    public static void main(String[] args) throws IOException, TimeoutException {
        new RpcServer().start();
    }
}
