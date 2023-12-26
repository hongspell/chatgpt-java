package com.hong.chatgpt.entity.edit;

import com.hong.chatgpt.entity.common.Choice;
import com.hong.chatgpt.entity.common.Usage;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
public class EditResponse implements Serializable {
    private String id;
    private String object;
    private long created;
    private String model;
    private Choice[] choices;
    private Usage usage;
}
