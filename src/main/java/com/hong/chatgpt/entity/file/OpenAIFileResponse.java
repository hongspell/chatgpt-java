package com.hong.chatgpt.entity.file;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author hong
 * @Description Files are used to upload documents that can be used with features like Assistants and Fine-tuning.
 * @Date 2024-1-1
 **/
@Data
public class OpenAIFileResponse implements Serializable {

    private String id;
    private String object;
    private long bytes;
    @JsonProperty("created_at")
    private long createdAt;
    private String filename;
    private String purpose;

}
