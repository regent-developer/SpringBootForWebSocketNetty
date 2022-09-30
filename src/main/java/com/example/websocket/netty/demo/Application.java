package com.example.websocket.netty.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.example.websocket.netty.demo.netty.NettyServer;

@SpringBootApplication
public class Application {

	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
		
		try {
            new NettyServer(8000).start();
        } catch (Exception e) {
            System.out.println("NettyServerError:" + e.getMessage());
        }
	}

}
