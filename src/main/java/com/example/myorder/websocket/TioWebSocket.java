package com.example.myorder.websocket;

import com.alibaba.fastjson.JSON;

import com.example.myorder.utils.VerifyUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.tio.common.starter.annotation.TioServerMsgHandler;
import org.tio.core.ChannelContext;
import org.tio.core.Tio;
import org.tio.http.common.HttpRequest;
import org.tio.http.common.HttpResponse;
import org.tio.websocket.common.WsRequest;
import org.tio.websocket.common.WsResponse;
import org.tio.websocket.server.handler.IWsMsgHandler;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ： zgc
 * @date ： 2020/8/17 14:44
 * @versions : 1.0
 * @project theone-xwyj
 * @content
 */
@TioServerMsgHandler
@Component
@RequiredArgsConstructor
public class TioWebSocket implements IWsMsgHandler {
    @Autowired
    private  VerifyUtil verifyUtil;

    private static final Map<String, ChannelContext> userSocketMap = new ConcurrentHashMap<>();

    @Override
    public HttpResponse handshake(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) {
        return httpResponse;
    }

    //http握手成功后触发该方法，一般用于绑定一些参数
    @Override
    public void onAfterHandshaked(HttpRequest httpRequest, HttpResponse httpResponse, ChannelContext channelContext) {
        Message message = new Message();
        String token = verifyUtil.getToken((HttpServletRequest) httpRequest, (HttpServletResponse) httpResponse);
        String userId = String.valueOf(verifyUtil.getUserIdByToken(token));
        if (userSocketMap.get(userId) == null) {
            userSocketMap.put(userId, channelContext);
            Tio.bindUser(channelContext, userId);
            message.setSuccess(true);
            message.setMsg(userId+ "  连接成功！");
            message.setCode(0);
            message.setMsgType(3);
        } else {
            userSocketMap.put(userId, channelContext);
            Tio.bindUser(channelContext, userId);
            message.setSuccess(false);
            message.setMsg(userId + "  请勿重复连接！");
            message.setCode(107);
            message.setMsgType(3);
        }
        //用tio-websocket，服务器发送到客户端的Packet都是WsResponse
        WsResponse wsResponse = WsResponse.fromText(JSON.toJSONString(message), "UTF-8");
        System.out.println("收到消息：" + JSON.toJSONString(message));
        //点对点发送
        if (userSocketMap != null && !userSocketMap.isEmpty()) {
            Tio.sendToUser(channelContext.tioConfig, userId, wsResponse);
        } else {
            Tio.send(channelContext, wsResponse);
        }
    }

    @Override
    public Object onBytes(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) {
        return null;
    }

    @Override
    public Object onClose(WsRequest wsRequest, byte[] bytes, ChannelContext channelContext) {
        return null;
    }

    @Override
    public WsResponse onText(WsRequest wsRequest, String text, ChannelContext channelContext) {
        Message message = new Message();
        try {
            // 解析收到的文本消息为 Message 对象
            message = Message.fromJson(text);
            System.out.println("收到消息：" + text);

            // 根据 Message 内封装的 chatType 实现私聊、群聊或广播
            message.handle(channelContext);

        } catch (Exception e) {
            System.err.println("处理文本消息异常：" + e.getMessage());
            // 组装错误消息并返回给客户端
            message.setSuccess(false);
            message.setMsg("消息处理异常：" + e.getMessage());
        }
        return WsResponse.fromText(JSON.toJSONString(message), "UTF-8");
    }

    public ChannelContext getChannelContextByUserId(String userId) {
        return userSocketMap.get(userId);
    }

    public void removeChannelContextByUserId(String userId) {
        userSocketMap.remove(userId);
    }

    public boolean joinGroupByUserID(String groupId, String userId) {
        ChannelContext channelContextByUserId = this.getChannelContextByUserId(userId);
        if (channelContextByUserId != null) {
            Tio.bindGroup(channelContextByUserId.tioConfig, userId, groupId);
            return true;
        }
        return false;
    }
}
