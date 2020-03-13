package com.jean.service;

import com.jean.model.User;

public interface UserService {
    public String getUser(String id);
    public void saveUser(User user);
}
