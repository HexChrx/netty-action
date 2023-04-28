package com.example.nettyaction.aio.common.model;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
public class User {
    private Long id;
    private String name;

    private String password;

    public User (){}

    public User(Long id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public Long getId() {
        return id;
    }

    public User setId(Long id) {
        this.id = id;
        return this;
    }

    public String getName() {
        return name;
    }

    public User setName(String name) {
        this.name = name;
        return this;
    }

    public String getPassword() {
        return password;
    }

    public User setPassword(String password) {
        this.password = password;
        return this;
    }
}
