package com.hong.chatgpt.exception;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * @Author hong
 * @Description //TODO
 * @Date 2022-12-05
 **/
@EqualsAndHashCode(callSuper = true)
@Data
public class CommonException extends RuntimeException{

    private final String errorCode;
    private final String solution;

    public CommonException(String message, String errorCode, String solution) {
        super(message);
        this.errorCode = errorCode;
        this.solution = solution;
    }

}
