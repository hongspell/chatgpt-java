package com.hong.chatgpt.entity.chat;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author hong
 * @Description //TODO
 * @Date 2022-12-05
 **/
@Data
public class ChatChoice implements Serializable {

    @JsonProperty("finish_reason")
    private String finishReason;
    private long index;
    @JsonProperty("message")
    private ChatMessage message;
    /**
     * return delta(same with message) when use streaming
     */
    @JsonProperty("delta")
    private ChatMessage delta;
    private Object logprobs;

}
