package com.hong.chatgpt.entity.finetuning;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
public class FineTuneResponse implements Serializable {

    private String object;
    private String id;
    private String model;
    @JsonProperty("created_at")
    private long createdAt;
    @JsonProperty("finished_at")
    private long finishedAt;
    @JsonProperty("fine_tuned_model")
    private String fineTunedModel;
    @JsonProperty("organization_id")
    private String organizationId;
    @JsonProperty("result_files")
    private List<String> resultFiles;
    private String status;
    @JsonProperty("validation_files")
    private String validationFiles;
    @JsonProperty("training_file")
    private String trainingFiles;
    private Hyperparameter hyperparameters;
    @JsonProperty("trained_tokens")
    private Integer trainedTokens;

}
