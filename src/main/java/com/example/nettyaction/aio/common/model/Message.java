package com.example.nettyaction.aio.common.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author ruixiang.crx
 * @date 2023/4/26
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Message {
    private String type;

    private User fromUser;

    private Long toUser;

    private byte[] content;
}
