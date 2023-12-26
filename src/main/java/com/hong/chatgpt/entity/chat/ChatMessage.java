package com.hong.chatgpt.entity.chat;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.jetbrains.annotations.NotNull;
import java.io.Serializable;


/**
 * @Author hong
 * @Description //TODO
 * @Date 2022-12-05
 **/
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ChatMessage implements Serializable {

    private String role;
    private String content;
    private String name;

    public static Builder builder(){
        return new Builder();
    }

    /**
     * @Description constructor
     * @Param [role, content, name]
     **/
    public ChatMessage(String role, String content, String name) {
        this.role = role;
        this.content = content;
        this.name = name;
    }

    public ChatMessage() {
    }

    private ChatMessage(Builder builder){
        setRole(builder.role);
        setContent(builder.content);
        setName(builder.name);
    }

    @Getter
    @AllArgsConstructor
    public enum Role {
        SYSTEM("system"),
        USER("user"),
        ASSISTANT("assistant"),
        ;
        private String name;
    }

    public static final class Builder {
        private @NotNull String role;
        private String content;
        private String name;

        public Builder() {
        }

        public Builder role(@NotNull Role role){
            this.role = role.getName();
            return this;
        }

        public Builder content(String content){
            this.content = content;
            return this;
        }

        public Builder name(String name){
            this.name = name;
            return this;
        }

        public ChatMessage build(){
            return new ChatMessage(this);
        }
    }

}
