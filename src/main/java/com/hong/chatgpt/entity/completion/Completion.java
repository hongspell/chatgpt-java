package com.hong.chatgpt.entity.completion;

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
 * @Description Completion
 * @Date
 **/
@Data
@Builder
@Slf4j
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class Completion implements Serializable {

    @NonNull
    @Builder.Default
    private String model = Model.DAVINCI_003.getName();

    /**
     * problem description
     */
    @NonNull
    private String prompt;

    /**
     * The suffix used to format the output result
     */
    private String suffix;

    /**
     * Max: 4096
     */
    @JsonProperty("max_tokens")
    @Builder.Default
    private Integer maxTokens = 2048;

    @Builder.Default
    private double temperature = 0;

    @JsonProperty("top_p")
    @Builder.Default
    private Double topP = 1d;

    /**
     * The number of completions generated for each prompt.
     */
    @Builder.Default
    private Integer n = 1;

    @Builder.Default
    private boolean stream = false;
    /**
     * Max: 5
     */
    private Integer logprobs;

    @Builder.Default
    private boolean echo = false;

    private List<String> stop;

    @JsonProperty("presence_penalty")
    @Builder.Default
    private double presencePenalty = 0;

    /**
     * -2.0 ~~ 2.0
     */
    @JsonProperty("frequency_penalty")
    @Builder.Default
    private double frequencyPenalty = 0;

    @JsonProperty("best_of")
    @Builder.Default
    private Integer bestOf = 1;

    @JsonProperty("logit_bias")
    private Map logitBias;

    /**
     * User unique value
     */
    private String user;

    /**
     * @Description get the number of tokens for the current parameter
     * @Param []
     * @return long
     **/
    public long tokens() {
        if (StrUtil.isBlank(this.prompt) || StrUtil.isBlank(this.model)) {
            log.warn("Parameter exception, model: {}, prompt：{}", this.model, this.prompt);
            return 0;
        }
        return TokenHelper.tokens(this.model, this.prompt);
    }

    @Getter
    @AllArgsConstructor
    public enum Model {
        DAVINCI_003("text-davinci-003"),
        DAVINCI_002("text-davinci-002"),
        DAVINCI("davinci"),
        ;
        private String name;
    }

}
