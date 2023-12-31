package com.hong.chatgpt.entity.image;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

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
public class Image implements Serializable {

    // Required, a text description of the desired image(s). The maximum length is 1000 characters for dall-e-2 and 4000 characters for dall-e-3.
    @NotNull
    private String prompt;

    // The model to use for image generation
    private String model = Model.DALL_E_2.getModel();

    // Defaults to 1, the number of images to generate. Must be between 1 and 10. For dall-e-3, only n=1 is supported.
    @Builder.Default
    private Integer n = 1;

    // Defaults to standard, the quality of the image that will be generated. This param is only supported for dall-e-3.
    private String quality = Quality.STANDARD.getName();

    // The format in which the generated images are returned. Must be one of url or b64_json
    @Builder.Default
    @JsonProperty("response_format")
    private String responseFormat = ResponseFormat.URL.getName();

    // The size of the generated images.
    // Must be one of 256x256, 512x512, or 1024x1024 for dall-e-2. Must be one of 1024x1024, 1792x1024, or 1024x1792 for dall-e-3 models.
    @Builder.Default
    private String size = Size.SIZE_1024.getName();

    // The style of the generated images. This param is only supported for dall-e-3.
    private String style = Style.VIVID.getName();

    private String user;

    @Getter
    @AllArgsConstructor
    public enum Quality implements Serializable {

        STANDARD("standard"),
        HD("hd"),
        ;
        private final String name;
    }


}


