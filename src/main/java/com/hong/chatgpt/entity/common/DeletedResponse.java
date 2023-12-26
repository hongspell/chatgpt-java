package com.hong.chatgpt.entity.common;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
public class DeletedResponse implements Serializable {
    private String id;
    private String object;
    private boolean deleted;
}
