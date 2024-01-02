package com.hong.chatgpt.entity.finetuning;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
public class Event implements Serializable {
    private String object;
    @JsonProperty("created_at")
    private long createdAt;
    private String level;
    private String message;
    private Object data;
    private String type;
}
