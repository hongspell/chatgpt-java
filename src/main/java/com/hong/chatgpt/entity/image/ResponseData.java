package com.hong.chatgpt.entity.image;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
public class ResponseData implements Serializable {

    // The URL of the generated image, if response_format is url (default).
    private String url;
    // The base64-encoded JSON of the generated image, if response_format is b64_json.
    @JsonProperty("b64_json")
    private String b64Json;
    // The prompt that was used to generate the image, if there was any revision to the prompt.
    @JsonProperty("revised_prompt")
    private String revisedPrompt;

}
