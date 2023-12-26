package com.hong.chatgpt.exception;

import lombok.Getter;

/**
 * @Author hong
 * @Description //TODO
 * @Date 2022-12-05
 **/
@Getter
public enum OpenAIError {

    INVALID_AUTHENTICATION(401, "Invalid Authentication", "Ensure the correct API key and requesting organization are being used."),
    INCORRECT_API_KEY(401, "Incorrect API key provided", "Ensure the API key used is correct, clear your browser cache, or generate a new one."),
    NOT_MEMBER_OF_ORGANIZATION(401, "You must be a member of an organization to use the API", "Contact us to get added to a new organization or ask your organization manager to invite you to an organization."),
    RATE_LIMIT_REACHED(429, "Rate limit reached for requests", "Pace your requests. Read the Rate limit guide."),
    QUOTA_EXCEEDED(429, "You exceeded your current quota, please check your plan and billing details", "Buy more credits or learn how to increase your limits."),
    SERVER_ERROR(500, "The server had an error while processing your request", "Retry your request after a brief wait and contact us if the issue persists. Check the status page."),
    SERVER_OVERLOADED(503, "The engine is currently overloaded, please try again later", "Please retry your requests after a brief wait.");

    private final int statusCode;
    private final String overview;
    private final String solution;

    OpenAIError(int statusCode, String overview, String solution) {
        this.statusCode = statusCode;
        this.overview = overview;
        this.solution = solution;
    }

    public static OpenAIError fromStatusCode(int statusCode) {
        for (OpenAIError errorCode : values()) {
            if (errorCode.getStatusCode() == statusCode) {
                return errorCode;
            }
        }
        return null;
    }

}
