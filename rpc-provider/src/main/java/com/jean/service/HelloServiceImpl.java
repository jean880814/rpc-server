package com.jean.service;

import com.jean.annotation.RpcService;

import java.io.Serializable;

@RpcService(value = HelloService.class)
public class HelloServiceImpl implements HelloService, Serializable {
    public String say(String name) {
        return "helloService" + name;
    }
}
