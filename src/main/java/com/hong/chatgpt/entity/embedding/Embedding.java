package com.hong.chatgpt.entity.embedding;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;

import java.io.Serializable;
import java.util.List;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Builder
@Data
public class Embedding implements Serializable {

    /**
     * Input text to embed, encoded as a string or array of tokens. It cannot be an empty string and any array must be 2048 or less.
     **/
    @NotNull
    private List<String> input;

    @NotNull
    @Builder.Default
    private String model = Model.TEXT_EMBEDDING_ADA_002.getModel();

    @JsonProperty("encoding_format")
    private Float encodingFormat;

    private String user;

    @Getter
    @AllArgsConstructor
    public enum Model {
        TEXT_EMBEDDING_ADA_002("text-embedding-ada-002"),
        ;
        private final String model;
    }

}
