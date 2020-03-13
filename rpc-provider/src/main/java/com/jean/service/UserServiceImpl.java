package com.jean.service;

import com.jean.model.User;
import com.jean.rpc.RpcService;

@RpcService(value = UserService.class,version = "v1.0")
public class UserServiceImpl implements UserService {
    public String getUser(String id) {
        System.out.println("getUser + " + id);
        return "hello jean";
    }

    public void saveUser(User user) {
        System.out.println(user);
    }
}
