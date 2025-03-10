package com.example.myorder;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.tio.websocket.starter.EnableTioWebSocketServer;

@SpringBootApplication
@EnableTioWebSocketServer
public class MyOrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(MyOrderApplication.class, args);
    }
}