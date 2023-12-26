package com.hong.chatgpt.entity.audio;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Builder
@Data
public class Transcription{

    @Builder.Default
    private String model = Whisper.Model.WHISPER_1.getModel();

    /**
     * The language of the input audio, supplying the input language in ISO-639-1 format
     **/
    private String language;

    /**
     * An optional text to guide the model's style or continue a previous audio segment. The prompt should match the audio language.
     **/
    private String prompt;

    @JsonProperty("response_format")
    @Builder.Default
    private String responseFormat = Whisper.ResponseFormat.JSON.getFormat();

    /**
     * The sampling temperature, between 0 and 1.
     **/
    @Builder.Default
    private Double temperature = 0d;

}
