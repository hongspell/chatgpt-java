package com.hong.chatgpt.exception;

import com.hong.chatgpt.utils.ResultReturned;
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

    private final int errorCode;
    private final String message;

    public CommonException(String message) {
        super(message);
        this.errorCode = ResultReturned.error().getCode();
        this.message = message;
    }

    public CommonException(int code, String message) {
        super(message);
        this.errorCode = code;
        this.message = message;
    }

    public CommonException(){
        super(ResultReturned.error().getMessage());
        this.errorCode = ResultReturned.error().getCode();
        this.message = ResultReturned.error().getMessage();
    }

}
