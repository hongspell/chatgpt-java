package com.hong.chatgpt.entity.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.io.Serializable;
import java.util.List;

/**
 * @Author hong
 * @Description //TODO
 * @Date 2022-12-05
 **/
public class Model implements Serializable {

    private String id;
    private String object;
    private long created;
    @JsonProperty("owned_by")
    private String ownedBy;
    @JsonProperty("permission")
    private List<Permission> permission;
    private String root;
    private Object parent;

}
