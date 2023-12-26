package com.hong.chatgpt.exception;

import org.springframework.web.client.HttpClientErrorException;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
public class OpenAIExceptionHandler {

    public static String handleException(HttpClientErrorException e) {
        OpenAIError openAIError = OpenAIError.fromStatusCode(e.getStatusCode().value());

        if (openAIError != null) {
            return openAIError.getOverview() + " " + openAIError.getSolution();
        } else {
            return "An unknown error occurred: " + e.getResponseBodyAsString();
        }
    }

}
