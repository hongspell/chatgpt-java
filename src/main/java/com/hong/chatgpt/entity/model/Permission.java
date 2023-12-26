package com.hong.chatgpt.entity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;

/**
 * @Author hong
 * @Description //TODO
 * @Date 2022-12-05
 **/
public class Permission implements Serializable {

    private String id;
    @JsonProperty("object")
    private String object;
    @JsonProperty("created")
    private long created;
    @JsonProperty("allow_create_engine")
    private boolean allowCreateEngine;
    @JsonProperty("allow_sampling")
    private boolean allowSampling;
    @JsonProperty("allow_logprobs")
    private boolean allowLogprobs;
    @JsonProperty("allow_search_indices")
    private boolean allowSearchIndices;
    @JsonProperty("allow_view")
    private boolean allowView;
    @JsonProperty("allow_fine_tuning")
    private boolean allowFineTuning;
    @JsonProperty("organization")
    private String organization;
    @JsonProperty("group")
    private Object group;
    @JsonProperty("is_blocking")
    private boolean isBlocking;
}
