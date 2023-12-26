package com.hong.chatgpt.entity.completion;

import com.hong.chatgpt.entity.common.Choice;
import com.hong.chatgpt.entity.common.OpenAIResponse;
import com.hong.chatgpt.entity.common.Usage;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CompletionResponse extends OpenAIResponse implements Serializable {
    private String id;
    private String object;
    private long created;
    private String model;
    private Choice[] choices;
    private Usage usage;
}
