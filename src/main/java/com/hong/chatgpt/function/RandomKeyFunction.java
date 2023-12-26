package com.hong.chatgpt.function;

import cn.hutool.core.util.RandomUtil;

import java.util.List;
import java.util.function.Function;

/**
 * @Author hong
 * @Description get a key from a list randomly
 * @Date
 **/
public class RandomKeyFunction implements Function<List<String>, String> {
    @Override
    public String apply(List<String> keys) {
        return RandomUtil.randomEle(keys);
    }
}
