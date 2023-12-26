package com.hong.chatgpt.commandline;

import com.hong.chatgpt.entity.chat.ChatCompletionResponse;
import com.hong.chatgpt.entity.chat.ChatMessage;
import com.hong.chatgpt.service.OpenAIService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Scanner;

/**
 * @Author hong
 * @Description chat with openai in commandline
 * @Date 12/26/2023
 **/

@Component
public class ChatCommandLineRunner implements CommandLineRunner {

    private final OpenAIService service;

    public ChatCommandLineRunner(OpenAIService service) {
        this.service = service;
    }

    @Override
    public void run(String... args) throws Exception {
        try (Scanner scanner = new Scanner(System.in)) {
            System.out.println("Chat with OpenAI (type 'exit' to quit)");

            while (true) {
                System.out.print("You: ");
                String input = scanner.nextLine();

                if ("exit".equalsIgnoreCase(input)) {
                    break;
                }

                // build ChatMessage
                ChatMessage message = ChatMessage.builder().role(ChatMessage.Role.USER).content(input).build();

                // 假设 chatCompletion 方法是同步的
                Mono<ChatCompletionResponse> responseMono = service.chatCompletion(Collections.singletonList(message));

                ChatCompletionResponse response = responseMono.block();
                assert response != null;
                response.getChoices().forEach(e -> {
                    System.out.println("OpenAI: " +e.getMessage().getContent());
                });
            }
        }
    }
}
