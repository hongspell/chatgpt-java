package com.hong.chatgpt.entity.embedding;

import com.hong.chatgpt.entity.common.Usage;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
public class EmbeddingResponse implements Serializable {

    private String object;
    private List<EmbeddingVector> data;
    private String model;
    private Usage usage;

}
