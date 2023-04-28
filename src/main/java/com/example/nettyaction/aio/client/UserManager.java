package com.example.nettyaction.aio.client;


import com.example.nettyaction.aio.common.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public class UserManager {
    private static final Map<Long, User> USERS = new ConcurrentHashMap<>();

    public static void put(User user) {
        USERS.put(user.getId(), user);
    }

    public static User getUserInfo(Long id) {
        return USERS.get(id);
    }

}
