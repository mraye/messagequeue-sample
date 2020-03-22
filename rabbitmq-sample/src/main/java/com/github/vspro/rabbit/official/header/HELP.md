
## Headers类型

**虽然API需要指定route_key,但是对于 Headers 类型，是会忽略route_key**


指定不一样的route_key 和 binding_key

生产者：  

```console

input headers number: 1
input headers key and value, separated by space: key value
input message you want to sent(and 'bye' exits): hello
producer sent [x]: hello
input message you want to sent(and 'bye' exits): ha ha
producer sent [x]: ha ha
input message you want to sent(and 'bye' exits): bye
``` 

消费者:  

```console

input headers number: 1
input headers key and value, separated by space: key value
consumer [*] Waiting for messages. To exit press CTRL+C
consumer received [1]: hello
consumer received [2]: ha ha
```
