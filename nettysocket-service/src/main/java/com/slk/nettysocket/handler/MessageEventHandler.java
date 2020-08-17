package com.slk.nettysocket.handler;

import java.util.Date;
import java.util.UUID;

import com.corundumstudio.socketio.store.pubsub.ConnectMessage;
import com.slk.nettysocket.entity.ClientInfo;
import com.slk.nettysocket.entity.MessageInfo;
import com.slk.nettysocket.repository.ClientInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;
import org.redisson.api.listener.MessageListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.corundumstudio.socketio.AckRequest;
import com.corundumstudio.socketio.SocketIOClient;
import com.corundumstudio.socketio.SocketIOServer;
import com.corundumstudio.socketio.annotation.OnConnect;
import com.corundumstudio.socketio.annotation.OnDisconnect;
import com.corundumstudio.socketio.annotation.OnEvent;

/**
 * 消息处理类
 */
@Slf4j
@Component
public class MessageEventHandler {
    private final SocketIOServer server;

    @Autowired
    private ClientInfoRepository clientInfoRepository;

    @Autowired
    private RedissonClient redisson;

    @Autowired
    public MessageEventHandler(SocketIOServer server) {
        this.server = server;
    }

    //添加connect事件，当客户端发起连接时调用，本文中将clientid与sessionid存入数据库
    //方便后面发送消息时查找到对应的目标client,
    @OnConnect
    public void onConnect(SocketIOClient client) {
        // 存储client的信息，默认是存储在内存中，可通过redisson将信息存储到redis中，是以sessionId为key
        // client.set("clientId",111);
        System.out.println("sessionID:"+client.getSessionId());
        RTopic topic = redisson.getTopic("connected");
        topic.publish(new ConnectMessage(client.getSessionId()));


        String clientId = client.getHandshakeData().getSingleUrlParam("clientid");
        ClientInfo clientInfo = clientInfoRepository.findClientInfoByClientid(clientId);
        if (clientInfo != null) {
            Date nowTime = new Date(System.currentTimeMillis());
            clientInfo.setConnected((short) 1);
            clientInfo.setMostsignbits(client.getSessionId().getMostSignificantBits());
            clientInfo.setLeastsignbits(client.getSessionId().getLeastSignificantBits());
            clientInfo.setLastconnecteddate(nowTime);
            clientInfoRepository.save(clientInfo);
            //回发消息
            client.sendEvent("message", "onConnect back");
            log.info("客户端:" + client.getSessionId() + "已连接,clientId=" + clientId);
        }
    }

    //添加@OnDisconnect事件，客户端断开连接时调用，刷新客户端信息
    @OnDisconnect
    public void onDisconnect(SocketIOClient client) {
        RTopic topic = redisson.getTopic("connected");
        topic.addListener(ConnectMessage.class, (var1, var2) -> {
            log.debug("onMessage: {},{}", var2.getNodeId() ,var2.getSessionId());
            System.out.println("sessionID:"+var2.getSessionId());
        });


        String clientId = client.getHandshakeData().getSingleUrlParam("clientid");
        ClientInfo clientInfo = clientInfoRepository.findClientInfoByClientid(clientId);
        if (clientInfo != null) {
            clientInfo.setConnected((short) 0);
            clientInfo.setMostsignbits(null);
            clientInfo.setLeastsignbits(null);
            clientInfoRepository.save(clientInfo);
        }
    }

    //消息接收入口，当接收到消息后，查找发送目标客户端，并且向该客户端发送消息，且给自己发送消息
    @OnEvent(value = "messageevent")
    public void onEvent(SocketIOClient client, AckRequest request, MessageInfo data) {
//        System.out.println(client.get("clientId").toString());
        String targetClientId = data.getTargetClientId();
//        client.sendEvent("mesinfo", "sendData");
        // 广播
        server.getBroadcastOperations().sendEvent("mesinfo", "广播：sendData");
        ClientInfo clientInfo = clientInfoRepository.findClientInfoByClientid(targetClientId);
        if (clientInfo != null && clientInfo.getConnected() != 0) {
            UUID uuid = new UUID(clientInfo.getMostsignbits(), clientInfo.getLeastsignbits());
            System.out.println(uuid.toString());
            MessageInfo sendData = new MessageInfo();
            sendData.setSourceClientId(data.getSourceClientId());
            sendData.setTargetClientId(data.getTargetClientId());
            sendData.setMsgType("chat");
            sendData.setMsgContent(data.getMsgContent());
            client.sendEvent("mesinfo", sendData);
            server.getClient(uuid).sendEvent("mesinfo", sendData);
        }

    }
}