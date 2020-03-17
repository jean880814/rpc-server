package com.jean.service;

import com.jean.model.User;
import com.jean.annotation.RpcService;

import java.io.Serializable;

@RpcService(value = UserService.class)
public class UserServiceImpl implements UserService, Serializable {
    public String getUser(String id) {
        System.out.println("getUser + " + id);
        return "hello jean";
    }

    public void saveUser(User user) {
        System.out.println(user);
    }
}
