package com.hong.chatgpt.utils;

import lombok.Getter;

/**
 * @Author hong
 * @Description //TODO
 * @Date 2022-12-05
 **/
@Getter
public enum OpenAIResultCode {

    SUCCESS(200, "Success!"),

    ERROR(500, "Internal Server Error!"),

    EMPTY_PARAM(400, "Bad Request - Non-empty parameter required!"),

    BAD_PARAM(400, "Bad Request - Incorrect Parameter!"),

    USER_REGISTER_PARAMS_REPEAT(409, "Conflict - User registration information duplicated!"),

    USER_NOT_LOGIN(401, "Unauthorized - User not logged in!"),

    USER_NOT_EXIST(404, "Not Found - User mobile number not registered!"),

    USER_LOCKED(403, "Forbidden - Account locked, contact administrator!"),

    USER_CHAT_LIMITED(429, "Too Many Requests - User's daily chat function has reached its limit!"),

    USER_FILE_UPLOAD_LIMITED(429, "Too Many Requests - User's daily file upload function has reached its limit!"),

    ADMIN_OPERATE_FORBIDDEN(403, "Forbidden - Operation of administrator privileges forbidden!"),

    ADMIN_APIKEY_NULL(503, "Service Unavailable - System API-Key is busy!"),

    UPLOAD_FILE_ERROR(422, "Unprocessable Entity - File processing failed!"),
    ;

    private final int statusCode;
    private final String message;


    OpenAIResultCode(int statusCode, String message) {
        this.statusCode = statusCode;
        this.message = message;
    }

    public static OpenAIResultCode fromStatusCode(int statusCode) {
        for (OpenAIResultCode errorCode : values()) {
            if (errorCode.getStatusCode() == statusCode) {
                return errorCode;
            }
        }
        return null;
    }

}
