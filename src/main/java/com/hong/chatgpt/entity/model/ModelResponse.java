package com.hong.chatgpt.entity.model;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author hong
 * @Description //TODO
 * @Date 2022-12-05
 **/
@Data
public class ModelResponse implements Serializable {

    private String obj;
    private List<Model> data;

}
