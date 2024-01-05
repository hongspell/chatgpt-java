package com.hong.chatgpt.entity.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
@Slf4j
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ImageVariation implements Serializable {

    // The model to use for image generation
    @Builder.Default
    private String model = Model.DALL_E_2.getModel();

    @Builder.Default
    private Integer n = 1;

    @JsonProperty("response_format")
    @Builder.Default
    private String responseFormat = ResponseFormat.URL.getName();

    @Builder.Default
    private String size = Size.SIZE_1024.getName();

    private String user;
}
