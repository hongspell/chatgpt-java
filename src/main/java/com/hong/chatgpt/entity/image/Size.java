package com.hong.chatgpt.entity.image;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Size implements Serializable {

    SIZE_1792("1792x1024"),
    SIZE_1024("1024x1024"),
    SIZE_512("512x512"),
    SIZE_256("256x256"),
    ;
    private final String name;
}
