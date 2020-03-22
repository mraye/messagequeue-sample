package com.github.vspro.rabbit.official.hw;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

public class HwSend {


    public static void main(String[] args) throws IOException, TimeoutException {

        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);
        //设置端口会 java.net.ConnectException: Connection refused: connect ???
//        factory.setPort(Constants.PORT);

        try (Connection connection = factory.newConnection()) {
            Channel channel = connection.createChannel();
            channel.queueDeclare(Constants.Hw.QUEUE_NAME, false,false,false, null);
            String message = "hello rabbitmq";
            channel.basicPublish("", Constants.Hw.QUEUE_NAME, null, message.getBytes());
            System.out.println("producer sent [x]: "+ message);
        }

    }
}
