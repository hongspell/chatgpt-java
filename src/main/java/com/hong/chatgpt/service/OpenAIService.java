package com.hong.chatgpt.service;

import com.hong.chatgpt.entity.audio.Translation;
import com.hong.chatgpt.entity.chat.ChatCompletion;
import com.hong.chatgpt.entity.chat.ChatCompletionResponse;
import com.hong.chatgpt.entity.chat.ChatMessage;
import com.hong.chatgpt.entity.completion.Completion;
import com.hong.chatgpt.entity.completion.CompletionResponse;
import com.hong.chatgpt.entity.edit.Edit;
import com.hong.chatgpt.entity.edit.EditResponse;
import com.hong.chatgpt.entity.embedding.Embedding;
import com.hong.chatgpt.entity.embedding.EmbeddingResponse;
import com.hong.chatgpt.entity.model.Model;
import com.hong.chatgpt.entity.model.ModelResponse;
import com.hong.chatgpt.entity.audio.Transcription;
import com.hong.chatgpt.entity.audio.WhisperResponse;
import com.hong.chatgpt.exception.CommonException;
import com.hong.chatgpt.exception.OpenAIError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.File;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;

/**
 * @Author hong
 * @Description Implement some OpenAI interface's function
 * @Date
 **/
@Service
@Slf4j
public class OpenAIService {

    private final WebClient webClient;

    public OpenAIService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * @Description model list
     * @Param []
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.models.ModelResponse>
     **/
    public Mono<ModelResponse> getModels(){
        return this.webClient.get()
                .uri("/v1/models")
                .retrieve()
                .bodyToMono(ModelResponse.class);
    }

    /**
     * @Description model details
     * @Param [id]
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.models.Model>
     **/
    public Mono<Model> model(String id) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/models/{id}").build(id))
                .retrieve()
                .bodyToMono(Model.class);
    }

    /**
     * @Description Legacy models (2022-2023) text-davinci-003, text-davinci-002, davinci, curie, babbage, ada, gpt-3.5-turbo-instruct, babbage-002, davinci-002
     * @Param [completion]
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.completion.CompletionResponse>
     **/
    public Mono<CompletionResponse> completions(Completion completion) {
        return webClient.post()
                .uri("/v1/completions")
                .bodyValue(completion)
                .retrieve()
                .bodyToMono(CompletionResponse.class);
    }

    /**
     * @Description edit text
     * @Param [edit]
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.edit.EditResponse>
     **/
    public Mono<EditResponse> edits(Edit edit) {
        return webClient.post()
                .uri("/v1/edits")
                .bodyValue(edit)
                .retrieve()
                .bodyToMono(EditResponse.class);
    }

    /**
     * @Description Newer models (2023–) gpt-4, gpt-4 turbo, gpt-3.5-turbo
     * @Param [chatCompletion]
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.chat.ChatCompletionResponse>
     **/
    public Mono<ChatCompletionResponse> chatCompletion(ChatCompletion chatCompletion) {
        return webClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(chatCompletion)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(3)));
    }

    /**
     * @Description chat for command line
     * @Param [chatCompletion]
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.chat.ChatCompletionResponse>
     **/
    public Mono<ChatCompletionResponse> chatCompletion(List<ChatMessage> messages) {

        ChatCompletion chatCompletion = ChatCompletion.builder().messages(messages).build();

        return webClient.post()
                .uri("/v1/chat/completions")
                .bodyValue(chatCompletion)
                .retrieve()
                .bodyToMono(ChatCompletionResponse.class)
                .retryWhen(Retry.backoff(2, Duration.ofSeconds(3)));
    }

    /**
     * @Description Transcribes audio into the input language.
     * @Param [file, transcription]
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.speech.WhisperResponse>
     **/
    public Mono<WhisperResponse> speechToTranscriptions(File file, Transcription transcription){
        return webClient.post()
                .uri("/v1/audio/transcriptions")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createMultipartData(file, transcription)))
                .retrieve()
                .bodyToMono(WhisperResponse.class);
    }

    /**
     * @Description Translates audio into English.
     * @Param [file, translation]
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.audio.WhisperResponse>
     **/
    public Mono<WhisperResponse> speechToTranslations(File file, Translation translation){
        return webClient.post()
                .uri("/v1/audio/translations")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createMultipartData(file, translation)))
                .retrieve()
                .bodyToMono(WhisperResponse.class);
    }

    /**
     * @Description to handle form-data for translations & transcriptions in a method, if u don't like reflex, u can choose other way to achieve it
     * @Param [file, params]
     * @return org.springframework.util.MultiValueMap<java.lang.String,org.springframework.http.HttpEntity<?>>
     **/
    private MultiValueMap<String, HttpEntity<?>> createMultipartData(File file, Object params) {
        MultiValueMap<String, HttpEntity<?>> data = new LinkedMultiValueMap<>();

        // wrap resource
        data.add("file", new HttpEntity<>(new FileSystemResource(file)));

        // check fields with reflex
        Field[] fields = params.getClass().getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                Object value = field.get(params);
                if (value != null) {
                    // warning, the mapping between entity's fields and form，focusing on @JsonProperty
                    String fieldName = field.getName();
                    data.add(fieldName, new HttpEntity<>(value));
                }
            } catch (IllegalAccessException e) {
                log.error(e.getMessage());
            }
        }
        return data;
    }

//    private MultiValueMap<String, HttpEntity<?>> createMultipartDataForTranscriptions(File, Transcription transcription) {
//        MultiValueMap<String, HttpEntity<?>> data = new LinkedMultiValueMap<>();
//
//        // wrap the file resource
//        data.add("file", new HttpEntity<>(new FileSystemResource(file)));
//
//        // add other fields
//        if (transcription.getModel() != null) data.add("model", new HttpEntity<>(transcription.getModel()));
//        if (transcription.getLanguage() != null) data.add("language", new HttpEntity<>(transcription.getLanguage()));
//        if (transcription.getPrompt() != null) data.add("prompt", new HttpEntity<>(transcription.getPrompt()));
//        if (transcription.getResponseFormat() != null) data.add("response_format", new HttpEntity<>(transcription.getResponseFormat()));
//        if (transcription.getTemperature() != null) data.add("temperature", new HttpEntity<>(transcription.getTemperature()));
//
//        return data;
//    }


    public Mono<EmbeddingResponse> embeddings(Embedding embedding){
        return webClient.post()
                .uri("/v1/embeddings")
                .bodyValue(embedding)
                .retrieve()
                .bodyToMono(EmbeddingResponse.class);
    }


}
