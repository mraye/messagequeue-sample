
## 路由操作

**注意，理论上来讲，rabbitmq中的bindingKey 和 routeKey是相同的**

+ bindingKey: 绑定exchange和queue之间的关系
+ routeKey: 只有bindingKey和routeKey相等，exchange才会将消息发送到正确的队列上


消息生产者1，路由 error 类型的日志

```console

input route key: error
input message you want to sent(and 'bye' exits): error log
producer sent [x]: error log
input message you want to sent(and 'bye' exits): bye
```



消息生产者2，路由 info 类型的日志

```console

input route key: info
input message you want to sent(and 'bye' exits): info log
producer sent [x]: info log
input message you want to sent(and 'bye' exits): bye
```

消费者1， 绑定 info warn error 类型的 key

```console

input binding keys, separated by space: info warn error
consumer amq.gen-5Dllfg53zKmTHw9cho6OAQ [*] Waiting for messages. To exit press CTRL+C
consumer recv [x]: error log
consumer recv [x]: info log
```

消费者2， 绑定 info warn 类型的 key

```console

input binding keys, separated by space: info warn
consumer amq.gen-ZJM9oSYyborcXx4UgT0jnw [*] Waiting for messages. To exit press CTRL+C
consumer recv [x]: info log
```


***可以发现，消费者1和消费者2都可以消费到info类型的日志，但只有消费者1才能消费到error类型的日志***
