package com.hong.chatgpt.entity.common;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
public class Choice implements Serializable {

    private String text;
    private long index;
    private Object logprobs;
    @JsonProperty("finish_reason")
    private String finishReason;

}
