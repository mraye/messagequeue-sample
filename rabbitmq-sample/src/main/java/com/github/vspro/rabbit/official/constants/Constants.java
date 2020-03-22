package com.github.vspro.rabbit.official.constants;


/**
 * 常量
 */
public interface Constants {


    String HOST = "192.168.199.234";
    int PORT = 5762;

    interface Hw {

        String QUEUE_NAME = "hello";
    }


    interface WorkQueue {

        String QUEUE_NAME = "work-queue";
    }

    interface PubSub{

        String QUEUE_NAME = "pubsub-queue";
        String EXCHANGE_NAME = "pubsub-ex";
    }

    interface Routing{

        String EXCHANGE_NAME = "route-direct-ex";
    }

    interface Topic{

        String EXCHANGE_NAME = "topic-ex";
    }

    interface RPC{

        String QUEUE_NAME  = "rpc-queue";
    }

    interface Headers{

        String EXCHANGE_NAME = "header-ex";
        String QUEUE_NAME  = "header-queue";
        String ROUTE_KEY = "rk-header";
        String BINDING_KEY = "bdk-header";
    }
}
