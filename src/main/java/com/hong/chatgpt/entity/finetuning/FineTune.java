package com.hong.chatgpt.entity.finetuning;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FineTune implements Serializable {

    @NonNull
    private String model;

    @NonNull
    @JsonProperty("training_file")
    private String trainingFile;

    private Hyperparameter hyperparameters;

    private String suffix;

    @JsonProperty("validation_file")
    private String validationFile;

    @Getter
    @AllArgsConstructor
    public enum Model {
        BABBAGE("babbage-002"),
        DAVINCI("davinci-002"),
        ;
        private String name;
    }

}
