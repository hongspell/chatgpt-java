package com.hong.chatgpt.entity.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
public class OpenAIResponse<T> implements Serializable {

    private String object;
    private List<T> data;
    private Error error;


    @Data
    public static class Error {
        private String message;
        private String type;
        private String param;
        private String code;
    }

}
