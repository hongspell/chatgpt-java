package com.hong.chatgpt.entity.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
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
public class ImageEdit implements Serializable {

    @NonNull
    private String prompt;

    // Only dall-e-2 is supported at this time.
    @Builder.Default
    private String model = Model.DALL_E_2.getModel();

    // The number of images to generate. Must be between 1 and 10. Defaults to 1
    @Builder.Default
    private Integer n = 1;

    @Builder.Default
    private String size = Size.SIZE_1024.getName();

    @JsonProperty("response_format")
    @Builder.Default
    private String responseFormat = ResponseFormat.URL.getName();

    private String user;

}
