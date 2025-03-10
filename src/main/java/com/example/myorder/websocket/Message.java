package com.example.myorder.websocket;

import com.alibaba.fastjson.JSON;
import lombok.Data;
import java.io.Serializable;
import org.tio.websocket.common.WsResponse;
import org.tio.core.Tio;
import org.tio.core.ChannelContext;

/**
 * Socket 发送消息模板，封装消息解析与发送逻辑<br/>
 * 支持私聊、群聊和广播：<br/>
 * - chatType 为 "private" 时，使用 toUserID 发送私聊消息<br/>
 * - chatType 为 "group" 时，toUserID 表示群组ID，向该群组内所有客户端发送消息<br/>
 * - chatType 为 "broadcast" 时，向所有在线客户端广播消息
 */
@Data
@SuppressWarnings("serial")
public class Message implements Serializable {

    private static final long serialVersionUID = 1L;

    // 原有字段
    private Integer msgType; // 0事件信息 1预警信息 2地图同步 3系统返回信息 4心跳监测
    private Integer sendType; // 1发送 0接收
    private Boolean success;
    private String msg;
    private Object data;
    private Integer code;
    private String toUserID; // 接收人ID（私聊或群聊时使用）
    private String receiveTime; // 接收时间

    // 新增字段：用于区分聊天类型，可值 "private", "group", "broadcast"
    private String chatType;

    public Message() {
    }
    /**
     * 从 JSON 字符串解析出 Message 对象
     * 
     * @param jsonStr JSON 格式的消息字符串
     * @return Message 对象
     */
    public static Message fromJson(String jsonStr) {
        return JSON.parseObject(jsonStr, Message.class);
    }

    /**
     * 将当前 Message 对象转换为 WsResponse 对象，用于发送到客户端
     * 
     * @return WsResponse 对象
     */
    public WsResponse toWsResponse() {
        String jsonStr = JSON.toJSONString(this);
        return WsResponse.fromText(jsonStr, "utf-8");
    }

    /**
     * 根据 chatType 实现消息发送逻辑：
     * <ul>
     * <li>chatType 为 "private"：调用 Tio.sendToUser 发送私聊消息</li>
     * <li>chatType 为 "group"：调用 Tio.sendToGroup 向群组内所有客户端发送消息</li>
     * <li>chatType 为 "broadcast"：调用 Tio.sendToAll 广播消息</li>
     * </ul>
     * 若未设置 chatType，则默认按私聊处理。
     *
     * @param channelContext 当前 ChannelContext 对象
     * @throws Exception 发送过程中抛出的异常
     */
    public void handle(ChannelContext channelContext) throws Exception {
        WsResponse response = this.toWsResponse();
        if (chatType != null) {
            // 根据 chatType 分别处理私聊、群聊和广播
            // 0 代表私聊、1代表群聊、2代表广播
            if ("0".equalsIgnoreCase(chatType)) {
                Tio.sendToUser(channelContext.getTioConfig(), this.getToUserID(), response);
            } else if ("1".equalsIgnoreCase(chatType)) {
                Tio.sendToGroup(channelContext.getTioConfig(), this.getToUserID(), response);
            } else if ("2".equalsIgnoreCase(chatType)) {
                Tio.sendToAll(channelContext.getTioConfig(), response);
            } else {
                // 未知类型时默认当作私聊处理
                Tio.sendToUser(channelContext.getTioConfig(), this.getToUserID(), response);
            }
        } else {
            // 若未指定 chatType，默认视为私聊消息
            Tio.sendToUser(channelContext.getTioConfig(), this.getToUserID(), response);
        }
    }
}
