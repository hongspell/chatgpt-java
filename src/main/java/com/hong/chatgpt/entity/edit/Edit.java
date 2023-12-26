package com.hong.chatgpt.entity.edit;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.Serializable;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
@Builder
@Slf4j
@NoArgsConstructor
@AllArgsConstructor
public class Edit implements Serializable {
    /**
     * edit model
     */
    @NonNull
    private String model;

    @NonNull
    private String input;
    /**
     * tells you how to modify it
     */
    @NonNull
    private String instruction;

    @Builder.Default
    private double temperature = 0;

    @JsonProperty("top_p")
    @Builder.Default
    private Double topP = 1d;

    /**
     * The number of completions generated for each prompt
     */
    @Builder.Default
    private Integer n = 1;

    public void setModel(Model model) {
        this.model = model.getName();
    }

    public void setTemperature(double temperature) {
        if (temperature > 2 || temperature < 0) {
            log.error("Temperature parameter anomaly, temperature belongs [0,2]");
            this.temperature = 1;
            return;
        }

        this.temperature = temperature;
    }

    @Getter
    @AllArgsConstructor
    public enum Model {
        TEXT_DAVINCI_EDIT_001("text-davinci-edit-001"),
        CODE_DAVINCI_EDIT_001("code-davinci-edit-001"),
        ;
        private final String name;
    }
}
