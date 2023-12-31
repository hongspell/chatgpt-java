package com.hong.chatgpt.entity.image;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;

@Getter
@AllArgsConstructor
public enum Style implements Serializable {

    // Vivid causes the model to lean towards generating hyper-real and dramatic images.
    VIVID("vivid"),
    // Natural causes the model to produce more natural, less hyper-real looking images.
    NATURAL("natural"),
    ;
    private final String name;
}
