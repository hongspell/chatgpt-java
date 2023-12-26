package com.hong.chatgpt.entity.chat;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import com.hong.chatgpt.utils.TokenHelper;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Author hong
 * @Description //TODO
 * @Date 2022-12-05
 **/
@Data
@Builder
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ChatCompletion implements Serializable {

    @NonNull
    @Builder.Default
    private String model = Model.GPT_3_5_TURBO.getName();

    /**
     * Question description
     */
    @NonNull
    private List<ChatMessage> messages;

    /**
     * What sampling temperature to use, between 0 and 2.
     * Higher values (e.g., 0.8) make the output more random,
     * while lower values (e.g., 0.2) make it more focused and deterministic.
     **/
    @Builder.Default
    private double temperature = 0.2;

    /**
     * An alternative method to temperature sampling called nucleus sampling,
     * where the model considers results of tokens with top_p probability mass.
     * Thus, 0.1 means only considering tokens that contain the top 10% probability mass.
     */
    @JsonProperty("top_p")
    @Builder.Default
    private Double topP = 1d;

    /**
     * Number of completions to generate for each prompt.
     */
    @Builder.Default
    private Integer n = 1;

    /**
     * Whether to output in a streaming manner.
     * default:false
     *
     */
    @Builder.Default
    private boolean stream = false;

    /**
     * Stop tokens for output termination
     */
    private List<String> stop;

    /**
     * Maximum supported is 4096, defaulted is 2048
     */
    @JsonProperty("max_tokens")
    @Builder.Default
    private Integer maxTokens = 2048;

    @JsonProperty("presence_penalty")
    @Builder.Default
    private double presencePenalty = 0;

    /**
     * Range from -2.0 ~ 2.0
     */
    @JsonProperty("frequency_penalty")
    @Builder.Default
    private double frequencyPenalty = 0;

    @JsonProperty("logit_bias")
    private Map<String, Integer> logitBias;

    /**
     * Unique value for user, ensuring the interface is not repeatedly called
     */
    private String user;

    /**
     * @Description Get the number of tokens for the current parameters
     * @Param []
     * @return long
     **/
    public long tokens() {
        if (CollectionUtil.isEmpty(this.messages) || StrUtil.isBlank(this.model)) {
            log.warn("parameter exception - model: [{}], messages: [{}]", this.model, this.messages);
            return 0;
        }
        return TokenHelper.tokens(this.model, this.messages);
    }

    @Getter
    @AllArgsConstructor
    public enum Model {
        /**
         * gpt-3.5-turbo
         */
        GPT_3_5_TURBO("gpt-3.5-turbo"),
        /**
         * Not recommended
         */
        GPT_3_5_TURBO_0301("gpt-3.5-turbo-0301"),
        /**
         * GPT4.0
         */
        GPT_4("gpt-4"),
        /**
         * Not recommended
         */
        GPT_4_0314("gpt-4-0314"),
        /**
         * GPT4.0 for long context
         */
        GPT_4_32K("gpt-4-32k"),
        /**
         * Not recommended
         */
        GPT_4_32K_0314("gpt-4-32k-0314"),
        ;
        private final String name;
    }
}
