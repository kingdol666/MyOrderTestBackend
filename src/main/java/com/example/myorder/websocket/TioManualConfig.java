package com.example.myorder.websocket;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.tio.websocket.server.WsServerStarter;

import java.io.IOException;

@Configuration
public class TioManualConfig {
    @Value("${tio.websocket.server.port:9326}")
    private int websocketPort;


    @Bean(initMethod = "start")
    public WsServerStarter wsServerStarter() throws IOException {
        return new WsServerStarter(websocketPort, new TioWebSocket());
    }
}
