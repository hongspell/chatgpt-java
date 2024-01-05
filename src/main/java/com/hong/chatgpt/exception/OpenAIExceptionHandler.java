package com.hong.chatgpt.exception;

import com.hong.chatgpt.utils.OpenAIResultCode;
import org.springframework.web.client.HttpClientErrorException;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
public class OpenAIExceptionHandler {

    public static String handleException(HttpClientErrorException e) {
        OpenAIResultCode openAIError = OpenAIResultCode.fromStatusCode(e.getStatusCode().value());

        if (openAIError != null) {
            return openAIError.getStatusCode() + " " + openAIError.getMessage();
        } else {
            return "An unknown error occurred: " + e.getResponseBodyAsString();
        }
    }

}
