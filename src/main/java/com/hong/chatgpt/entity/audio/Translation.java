package com.hong.chatgpt.entity.audio;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
@Builder
public class Translation{

    @Builder.Default
    private String model = Whisper.Model.WHISPER_1.getModel();

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
