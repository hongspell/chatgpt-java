package com.hong.chatgpt.utils;

import cn.hutool.core.util.StrUtil;
import com.hong.chatgpt.entity.chat.ChatCompletion;
import com.hong.chatgpt.entity.chat.ChatMessage;
import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.EncodingType;
import com.knuddels.jtokkit.api.ModelType;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * @Author hong
 * @Description //TODO
 * @Date 2022-12-05
 **/
@Slf4j
public class TokenHelper {

    /**
     * model's name matches Encoding
     */
    private static final Map<String, Encoding> modelMap = new HashMap<>();

    /**
     * registry entity
     */
    private static final EncodingRegistry registry = Encodings.newDefaultEncodingRegistry();

    static {
        for (ModelType modelType : ModelType.values()) {
            modelMap.put(modelType.getName(), registry.getEncodingForModel(modelType));
        }
        modelMap.put(ChatCompletion.Model.GPT_3_5_TURBO_0301.getName(), registry.getEncodingForModel(ModelType.GPT_3_5_TURBO));
        modelMap.put(ChatCompletion.Model.GPT_4_32K.getName(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ChatCompletion.Model.GPT_4_32K_0314.getName(), registry.getEncodingForModel(ModelType.GPT_4));
        modelMap.put(ChatCompletion.Model.GPT_4_0314.getName(), registry.getEncodingForModel(ModelType.GPT_4));
    }

    /**
     * @return java.util.List<java.lang.Integer>
     * @Description get encode list with encoding & text
     * @Param [enc, text]
     **/
    public static List<Integer> encode(@NotNull Encoding enc, String text) {
        return StrUtil.isBlank(text) ? new ArrayList<>() : enc.encode(text);
    }

    /**
     * @return int
     * @Description get tokens with encoding & text
     * @Param [enc, text]
     **/
    public static int tokens(@NotNull Encoding enc, String text) {
        return encode(enc, text).size();
    }

    /**
     * @return String
     * @Description get decode with encoding & text
     * @Param [enc, encoded]
     **/
    public static String decode(@NotNull Encoding enc, @NotNull List<Integer> encoded) {
        return enc.decode(encoded);
    }

    /**
     * @return com.knuddels.jtokkit.api.Encoding
     * @Description get an encoding obj with encoding type
     * @Param [encodingType]
     **/
    public static Encoding getEncoding(@NotNull EncodingType encodingType) {
        Encoding enc = registry.getEncoding(encodingType);
        return enc;
    }

    /**
     * @return java.util.List<java.lang.Integer>
     * @Description get encode list with encoding type & text
     * @Param [encodingType, text]
     **/
    public static List<Integer> encode(@NotNull EncodingType encodingType, String text) {
        if (StrUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        Encoding enc = getEncoding(encodingType);
        List<Integer> encoded = enc.encode(text);
        return encoded;
    }

    /**
     * @return int
     * @Description get a token with encodingtype & text
     * @Param [encodingType, text]
     **/
    public static int tokens(@NotNull EncodingType encodingType, String text) {
        return encode(encodingType, text).size();
    }

    /**
     * @return java.lang.String
     * @Description get decode with encodingtype & encodeed
     * @Param [encodingType, encoded]
     **/
    public static String decode(@NotNull EncodingType encodingType, @NotNull List<Integer> encoded) {
        Encoding enc = getEncoding(encodingType);
        return enc.decode(encoded);
    }

    /**
     * @return com.knuddels.jtokkit.api.Encoding
     * @Description get an encoding obj with a model name
     * @Param [modelName]
     **/
    public static Encoding getEncoding(@NotNull String modelName) {
        Encoding encoding = modelMap.get(modelName);
        if (encoding == null) {
            log.warn("[{}] model does not exist or does not support the calculation of tokens", modelName);
        }
        return encoding;
    }

    /**
     * @return java.util.List<java.lang.Integer>
     * @Description get encode list with model name
     * @Param [modelName, text]
     **/
    public static List<Integer> encode(@NotNull String modelName, String text) {
        if (StrUtil.isBlank(text)) {
            return new ArrayList<>();
        }
        Encoding enc = getEncoding(modelName);
        if (Objects.isNull(enc)) {
            log.warn("[{}] model does not exist or does not support the calculation of tokens，just return tokens==0", modelName);
            return new ArrayList<>();
        }
        return enc.encode(text);
    }

    /**
     * @return int
     * @Description get token with model name & text
     * @Param [modelName, text]
     **/
    public static int tokens(@NotNull String modelName, String text) {
        Encoding encoding = getEncoding(modelName);
        if (encoding == null) {
            return 0;
        }
        return encode(modelName, text).size();
    }

    /**
     * @return int
     * @Description get tokens with model name & messages
     * @Param [modelName, messages]
     **/
    public static int tokens(@NotNull String modelName, @NotNull List<ChatMessage> messages) {
        Encoding encoding = getEncoding(modelName);
        int tokensPerMessage = 0;
        int tokensPerName = 0;
        // 3.5 model
        if (modelName.equals("gpt-3.5-turbo-0301") || modelName.equals("gpt-3.5-turbo")) {
            tokensPerMessage = 4;
            tokensPerName = -1;
        }
        // 4.0 model
        if (modelName.equals("gpt-4") || modelName.equals("gpt-4-0314")) {
            tokensPerMessage = 3;
            tokensPerName = 1;
        }
        int sum = 0;
        for (ChatMessage msg : messages) {
            sum += tokensPerMessage;
            sum += tokens(encoding, msg.getContent());
            sum += tokens(encoding, msg.getRole());
            sum += tokens(encoding, msg.getName());
            if (StrUtil.isNotBlank(msg.getName())) {
                sum += tokensPerName;
            }
        }
        sum += 3;
        return sum;
    }

    /**
     * @return java.lang.String
     * @Description get decode with model name & encoded
     * @Param [modelName, encoded]
     **/
    public static String decode(@NotNull String modelName, @NotNull List<Integer> encoded) {
        Encoding enc = getEncoding(modelName);
        return enc.decode(encoded);
    }

    private static final Map<String, ModelType> nameToModelTypeMap = new HashMap<>();

    static {
        nameToModelTypeMap.put(ChatCompletion.Model.GPT_3_5_TURBO_0301.getName(), ModelType.GPT_3_5_TURBO);
        nameToModelTypeMap.put(ChatCompletion.Model.GPT_4_0314.getName(), ModelType.GPT_4);
        nameToModelTypeMap.put(ChatCompletion.Model.GPT_4_32K.getName(), ModelType.GPT_4);
        nameToModelTypeMap.put(ChatCompletion.Model.GPT_4_32K_0314.getName(), ModelType.GPT_4);
        nameToModelTypeMap.put(ChatCompletion.Model.GPT_4.getName(), ModelType.GPT_4);
    }

    /**
     * @return com.knuddels.jtokkit.api.ModelType
     * @Description get model type with model name
     * @Param [name]
     **/
    public static ModelType getModelTypeByName(String name) {
        return nameToModelTypeMap.getOrDefault(name, logAndReturnNull(name));
    }

    private static ModelType logAndReturnNull(String name) {
        log.warn("[{}] model does not exist or does not support the calculation of tokens", name);
        return null;
    }


}
