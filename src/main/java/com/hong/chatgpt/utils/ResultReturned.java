package com.hong.chatgpt.utils;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author hong
 * @Description //TODO
 * @Date 2024-01-05
 **/
@Data
public class ResultReturned {

    private Integer code;
    private String message;
    private Map<String, Object> data = new HashMap<>();

    public static ResultReturned success() {
        ResultReturned rr = new ResultReturned();
        rr.setCode(OpenAIResultCode.SUCCESS.getStatusCode());
        rr.setMessage(OpenAIResultCode.SUCCESS.getMessage());
        return rr;
    }

    public static ResultReturned error() {
        ResultReturned rr = new ResultReturned();
        rr.setCode(OpenAIResultCode.ERROR.getStatusCode());
        rr.setMessage(OpenAIResultCode.ERROR.getMessage());
        return rr;
    }

    public ResultReturned codeAndMessage(OpenAIResultCode code) {
        this.setCode(code.getStatusCode());
        this.setMessage(code.getMessage());
        return this;
    }

    public ResultReturned codeA(OpenAIResultCode code) {
        this.setCode(code.getStatusCode());
        return this;
    }

    public ResultReturned message(OpenAIResultCode code) {
        this.setMessage(code.getMessage());
        return this;
    }

    public ResultReturned data(String key, String value) {
        this.data.put(key, value);
        return this;
    }

    public ResultReturned data(Map<String,Object> map){
        this.setData(map);
        return this;
    }

}
