package com.hong.chatgpt.entity.file;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
public class OpenFilesWrapper implements Serializable {

    private List<OpenAIFileResponse> data;
    private String object;

}
