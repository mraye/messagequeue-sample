## Topic 

*消费方式大体和direct方式相同，唯一区别就是topic消费方式可能通过通配符方式匹配更多类型的路由*


生产者1

```console

input route key: kern.critical
input message you want to sent(and 'bye' exits): A critical kernel error
producer sent [x]: A critical kernel error
input message you want to sent(and 'bye' exits): bye
```


生产者2

```console

input route key: n.critical
input message you want to sent(and 'bye' exits): a critical log
producer sent [x]: a critical log
input message you want to sent(and 'bye' exits): bye
```


消费者1消费 `#` 类型的信息

```console
input binding keys, separated by space: #
consumer amq.gen-E69DxSR0yCkS_V9M-YgA1A [*] Waiting for messages. To exit press CTRL+C
consumer recv [x]: A critical kernel error
consumer recv [x]: a critical log
```



消费者2消费 `kern.*` 类型的信息
```console

input binding keys, separated by space: kern.*
consumer amq.gen-SkuJTLyzAIQZD3eiXgkj6A [*] Waiting for messages. To exit press CTRL+C
consumer recv [x]: A critical kernel error
```


消费者3消费 `*.critical` 类型的信息

```console 

input binding keys, separated by space: *.critical
consumer amq.gen-Xi-qLd7yddDleSDxS3fcDw [*] Waiting for messages. To exit press CTRL+C
consumer recv [x]: A critical kernel error
consumer recv [x]: a critical log
```

**可以发现消费者1消费了所有消息，而消费者2和消费者3只消费了与自己相关的消息**