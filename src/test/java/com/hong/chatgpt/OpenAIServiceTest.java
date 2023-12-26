package com.hong.chatgpt;

import com.hong.chatgpt.entity.audio.Translation;
import com.hong.chatgpt.entity.chat.ChatCompletion;
import com.hong.chatgpt.entity.chat.ChatCompletionResponse;
import com.hong.chatgpt.entity.chat.ChatMessage;
import com.hong.chatgpt.entity.audio.Transcription;
import com.hong.chatgpt.entity.audio.Whisper;
import com.hong.chatgpt.entity.audio.WhisperResponse;
import com.hong.chatgpt.entity.model.ModelResponse;
import com.hong.chatgpt.service.OpenAIService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.io.File;
import java.util.List;

/**
 * @Author hong
 * @Description for testing
 * @Date
 **/
@Slf4j
@SpringBootTest
@ExtendWith(SpringExtension.class)
public class OpenAIServiceTest {

    @Autowired
    private OpenAIService service;

    @Value("${openAIHost}")
    private String openAIHost;

    @Value("${secretKey}")
    private String apiKey;

    @BeforeEach
    public void setUp() {
        HttpClient httpClient = HttpClient.create()
                .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                        .host("127.0.0.1")
                        .port(7890));

        WebClient.Builder webClientBuilder = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(openAIHost)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .filter(ExchangeFilterFunction.ofResponseProcessor(clientResponse -> {
                    if (clientResponse.statusCode().is5xxServerError()) {
                        return Mono.error(new RuntimeException("System Error"));
                    }
                    return Mono.just(clientResponse);
                }));

        // new service for testing
        service = new OpenAIService(webClientBuilder);
    }

    @Test
    public void getModels(){
        Mono<ModelResponse> models = service.getModels();
        ModelResponse response = models.block();
        assert response != null;
        response.getData().forEach(
                System.out::println
        );
    }

    @Test
    public void chat() {
        ChatMessage message = ChatMessage.builder().role(ChatMessage.Role.USER).content("Hello my assistant！").build();
        ChatCompletion chatCompletion = ChatCompletion
                .builder()
                .messages(List.of(message))
                .model(ChatCompletion.Model.GPT_3_5_TURBO.getName())
                .build();
        Mono<ChatCompletionResponse> responseMono = service.chatCompletion(chatCompletion);
        ChatCompletionResponse response = responseMono.block();
        assert response != null;
        response.getChoices().forEach(e -> {
            System.out.println(e.getMessage());
        });
    }

    @Test
    public void speechToTranscriptions() {
        Transcription transcription = Transcription.builder()
                .model(Whisper.Model.WHISPER_1.getModel())
                .responseFormat(Whisper.ResponseFormat.JSON.getFormat())
                .prompt("prompt")
                .language("en")
                .temperature(0.5)
                .build();

        Mono<WhisperResponse> responseMono = service.speechToTranscriptions(new File("C:\\Users\\three\\Documents\\Sound Recordings\\Recording.m4a"), transcription);
        WhisperResponse response = responseMono.block();
        assert response != null;
        System.out.println(response.getText());
    }

    @Test
    public void speechToTranslations() {
        Translation translation = Translation.builder()
                .model(Whisper.Model.WHISPER_1.getModel())
                .responseFormat(Whisper.ResponseFormat.JSON.getFormat())
                .prompt("prompt")
                .temperature(0.5)
                .build();

        Mono<WhisperResponse> responseMono = service.speechToTranslations(new File("C:\\Users\\three\\Documents\\Sound Recordings\\Recording.m4a"), translation);
        WhisperResponse response = responseMono.block();
        assert response != null;
        System.out.println(response.getText());
    }
}
