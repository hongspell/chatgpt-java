package com.hong.chatgpt.entity.finetuning;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * @Author hong
 * @Description The hyper parameters used for the fine-tuning job.
 * @Date 2024/1/2
 **/
@Data
public class Hyperparameter implements Serializable {

    @JsonProperty("batch_size")
    private String batchSize;
    @JsonProperty("learning_rate_multiplier")
    private String learningRateMultiplier;
    @JsonProperty("n_epochs")
    private String nEpochs;

}
