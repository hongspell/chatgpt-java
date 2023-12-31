package com.hong.chatgpt.entity.image;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Model implements Serializable {

    DALL_E_2("dall-e-2"),
    DALL_E_3("dall-e-3"),
    ;
    private final String model;

}
