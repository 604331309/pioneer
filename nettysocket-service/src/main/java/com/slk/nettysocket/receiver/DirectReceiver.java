package com.slk.nettysocket.receiver;

import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * 自动ack
 */
@Component
@RabbitListener(queues = "TestDirectQueue")//监听的队列名称 TestDirectQueue
public class DirectReceiver {

    @RabbitHandler
    public void process(Channel channel, Message message, @Headers Map<String,Object> map) throws Exception {
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        System.out.println("DirectReceiver消费者收到消息  : " + message.toString());
        // 要尽量保证消息被ack，异常自行处理，不然有可能出现大量unacked状态的消息
        channel.basicAck(deliveryTag,false);
    }

}