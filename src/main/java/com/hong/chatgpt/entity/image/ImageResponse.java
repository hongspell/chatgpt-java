package com.hong.chatgpt.entity.image;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @Author hong
 * @Description //TODO
 * @Date
 **/
@Data
public class ImageResponse implements Serializable {

    private long created;
    private List<ResponseData> data;

}
