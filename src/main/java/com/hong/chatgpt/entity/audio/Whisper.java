package com.hong.chatgpt.entity.audio;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;

import java.io.Serializable;

/**
 * @Author hong
 * @Description a model that supports languages
 * @Date 12/25/2023
 **/
public interface Whisper{

    @Getter
    @AllArgsConstructor
    public enum Model {
        // ID of the model to use. Only whisper-1 is currently available
        WHISPER_1("whisper-1"),
        ;
        private final String model;
    }

    @Getter
    @AllArgsConstructor
    public enum ResponseFormat {
        // The format of the transcript output
        JSON("json"),
        TEXT("text"),
        SRT("srt"),
        VERBOSE_JSON("verbose_json"),
        VTT("vtt"),
        ;
        private final String format;
    }
}
