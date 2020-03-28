package com.jean.socketRpc;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.jean.service")
public class RpcConfig {

    @Bean
    public RpcBean rpcBean(){
        return new RpcBean(8080);
    }
}
