package com.hong.chatgpt.entity.audio;

import lombok.Data;

import java.io.Serializable;

/**
 * @Author hong
 * @Description thr response of whisper
 * @Date 12/25/2023
 **/
@Data
public class WhisperResponse implements Serializable {

    String text;

}
