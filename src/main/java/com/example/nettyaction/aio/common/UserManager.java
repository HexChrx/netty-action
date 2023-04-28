package com.example.nettyaction.aio.common;


import com.example.nettyaction.aio.common.model.User;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public class UserManager {
    private static final Map<Long, User> USERS = new ConcurrentHashMap<>();

    private static final AtomicLong CURRENT_USER_ID = new AtomicLong(1);

    static {
        createUser("SYSTEM", "SYSTEM");
    }

    public static User createUser(String userName, String password) {
        User user = new User(CURRENT_USER_ID.getAndIncrement(), userName, password);
        USERS.put(user.getId(), user);
        return user;
    }

    public static User getUserInfo(Long id) {
        return USERS.get(id);
    }

    public static User getSystemUser() {
        return USERS.get(1L);
    }
}
