package com.github.vspro.rabbit.official.pubconfirm;

import com.github.vspro.rabbit.official.constants.Constants;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.ConfirmCallback;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.ConcurrentNavigableMap;
import java.util.concurrent.ConcurrentSkipListMap;
import java.util.concurrent.TimeoutException;
import java.util.function.BooleanSupplier;

public class PublisherConfirms {

    public static final int COUNT = 50_000;


    static Connection newConnection() throws IOException, TimeoutException {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost(Constants.HOST);
        factory.setUsername("guest");
        factory.setPassword("guest");
        return factory.newConnection();
    }


    public static void main(String[] args) throws Exception {
        publishMessagesIndividually();
        publishMessagesInBatch();
        handlePublishConfirmsAsynchronously();
    }

    private static void handlePublishConfirmsAsynchronously() throws Exception {
        try (Connection connection = newConnection()) {
            Channel channel = connection.createChannel();

            String queue = UUID.randomUUID().toString();
            channel.queueDeclare(queue, false, false, false, null);
            channel.confirmSelect();


            ConcurrentNavigableMap<Long, String> outStandingConfirms = new ConcurrentSkipListMap<>();

            ConfirmCallback cleanOutStandingConfirms = (deliveryTag, multiple) -> {
                if (multiple) {
                    ConcurrentNavigableMap<Long, String> confirms = outStandingConfirms.headMap(deliveryTag, true);
                    confirms.clear();

                } else {
                    outStandingConfirms.remove(deliveryTag);
                }

            };

            channel.addConfirmListener(cleanOutStandingConfirms, (deliveryTag, multiple) -> {
                String body = outStandingConfirms.get(deliveryTag);
                System.err.format(
                        "Message with body %s has been nack-ed. Sequence number: %d, multiple: %b%n",
                        body, deliveryTag, multiple
                );
                cleanOutStandingConfirms.handle(deliveryTag, multiple);
            });

            long start = System.nanoTime();
            for (int i = 0; i < COUNT; i++) {
                String message = "confirm message " + String.valueOf(i);
                outStandingConfirms.put(channel.getNextPublishSeqNo(), message);
                channel.basicPublish("", queue, null, message.getBytes());
            }

            if (!waitUtil(Duration.ofSeconds(60), () -> outStandingConfirms.isEmpty())) {
                throw new IllegalStateException("All messages could not be confirmed in 60 seconds");
            }
            long end = System.nanoTime();
            System.out.format("Published %,d messages and handled confirms asynchronously in %,d ms%n", COUNT, Duration.ofNanos(end - start).toMillis());
        }

    }

    private static boolean waitUtil(Duration timeOut, BooleanSupplier condition) throws InterruptedException {
        int wait = 0;
        while (!condition.getAsBoolean() && wait < timeOut.toMillis()) {

            Thread.sleep(100L);
            wait += 100;
        }
        return condition.getAsBoolean();
    }


    private static void publishMessagesInBatch() throws Exception {
        try (Connection connection = newConnection()) {
            Channel channel = connection.createChannel();

            String queue = UUID.randomUUID().toString();
            channel.queueDeclare(queue, false, false, false, null);

            channel.confirmSelect();
            int batchSize = 100;

            int outStandingMessageCount = 0;
            long start = System.nanoTime();
            for (int i = 0; i < COUNT; i++) {
                String message = "confirm message " + String.valueOf(i);
                channel.basicPublish("", queue, null, message.getBytes());
                outStandingMessageCount++;
                if (outStandingMessageCount == batchSize) {

                    channel.waitForConfirmsOrDie(5_000);
                    outStandingMessageCount = 0;
                }
            }

            if (outStandingMessageCount > 0) {
                channel.waitForConfirmsOrDie(5_000);
            }
            long end = System.nanoTime();
            System.out.format("Published %,d messages in batch in  %,d ms%n", COUNT, Duration.ofNanos(end - start).toMillis());

        }
    }

    private static void publishMessagesIndividually() throws Exception {

        try (Connection connection = newConnection()) {
            Channel channel = connection.createChannel();

            String queue = UUID.randomUUID().toString();
            channel.queueDeclare(queue, false, false, false, null);
            channel.confirmSelect();
            long start = System.nanoTime();
            for (int i = 0; i < COUNT; i++) {
                String message = "confirm message " + String.valueOf(i);
                channel.basicPublish("", queue, null, message.getBytes());
                channel.waitForConfirmsOrDie(5_000);
            }
            long end = System.nanoTime();
            System.out.format("Published %,d messages individually in %,d ms%n", COUNT, Duration.ofNanos(end - start).toMillis());
        }
    }


}
