package com.hong.chatgpt.entity.embedding;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
public class EmbeddingVector implements Serializable {

    private String object;
    private List<Float> embedding;
    private Integer index;

}
