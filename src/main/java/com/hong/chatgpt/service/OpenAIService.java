package com.hong.chatgpt.service;

import com.hong.chatgpt.entity.audio.Translation;
import com.hong.chatgpt.entity.chat.ChatCompletion;
import com.hong.chatgpt.entity.chat.ChatCompletionResponse;
import com.hong.chatgpt.entity.chat.ChatMessage;
import com.hong.chatgpt.entity.common.DeletedResponse;
import com.hong.chatgpt.entity.common.OpenAIResponse;
import com.hong.chatgpt.entity.completion.Completion;
import com.hong.chatgpt.entity.completion.CompletionResponse;
import com.hong.chatgpt.entity.edit.Edit;
import com.hong.chatgpt.entity.edit.EditResponse;
import com.hong.chatgpt.entity.embedding.Embedding;
import com.hong.chatgpt.entity.embedding.EmbeddingResponse;
import com.hong.chatgpt.entity.file.OpenAIFileResponse;
import com.hong.chatgpt.entity.file.OpenFilesWrapper;
import com.hong.chatgpt.entity.finetuning.Event;
import com.hong.chatgpt.entity.finetuning.FineTune;
import com.hong.chatgpt.entity.finetuning.FineTuneResponse;
import com.hong.chatgpt.entity.image.*;
import com.hong.chatgpt.entity.model.Model;
import com.hong.chatgpt.entity.model.ModelResponse;
import com.hong.chatgpt.entity.audio.Transcription;
import com.hong.chatgpt.entity.audio.WhisperResponse;
import com.hong.chatgpt.exception.CommonException;
import com.hong.chatgpt.exception.OpenAIError;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriBuilder;
import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

import java.io.File;
import java.lang.reflect.Field;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * @Author hong
 * @Description Implement some OpenAI interface's function
 * @Date 2022-12-21
 **/
@Service
@Slf4j
public class OpenAIService {

    private final WebClient webClient;

    private static final long MAX_IMAGE_SIZE = 4 * 1024 * 1024; // 4MB
    private static final String SUPPORTED_FORMAT = "png";

    public OpenAIService(WebClient.Builder builder) {
        this.webClient = builder.build();
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.models.ModelResponse>
     * @Description model list
     * @Param []
     **/
    public Mono<ModelResponse> getModels() {
        return this.webClient.get()
                .uri("/v1/models")
                .retrieve()
                .bodyToMono(ModelResponse.class);
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.models.Model>
     * @Description model details
     * @Param [id]
     **/
    public Mono<Model> model(String id) {
        return this.webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/models/{id}").build(id))
                .retrieve()
                .bodyToMono(Model.class);
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.completion.CompletionResponse>
     * @Description Legacy models (2022-2023) text-davinci-003, text-davinci-002, davinci, curie, babbage, ada, gpt-3.5-turbo-instruct, babbage-002, davinci-002
     * @Param [completion]
     **/
    public Mono<CompletionResponse> completions(Completion completion) {
        return webClient.post()
                .uri("/v1/completions")
                .bodyValue(completion)
                .retrieve()
                .bodyToMono(CompletionResponse.class);
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.edit.EditResponse>
     * @Description edit text
     * @Param [edit]
     **/
    public Mono<EditResponse> edits(Edit edit) {
        return webClient.post()
                .uri("/v1/edits")
                .bodyValue(edit)
                .retrieve()
                .bodyToMono(EditResponse.class);
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.chat.ChatCompletionResponse>
     * @Description Newer models (2023–) gpt-4, gpt-4 turbo, gpt-3.5-turbo
     * @Param [chatCompletion]
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
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.chat.ChatCompletionResponse>
     * @Description chat for command line
     * @Param [chatCompletion]
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
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.speech.WhisperResponse>
     * @Description Transcribes audio into the input language.
     * @Param [file, transcription]
     **/
    public Mono<WhisperResponse> speechToTranscriptions(File file, Transcription transcription) {
        return webClient.post()
                .uri("/v1/audio/transcriptions")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createMultipartData(file, transcription)))
                .retrieve()
                .bodyToMono(WhisperResponse.class);
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.audio.WhisperResponse>
     * @Description Translates audio into English.
     * @Param [file, translation]
     **/
    public Mono<WhisperResponse> speechToTranslations(File file, Translation translation) {
        return webClient.post()
                .uri("/v1/audio/translations")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createMultipartData(file, translation)))
                .retrieve()
                .bodyToMono(WhisperResponse.class);
    }

    /**
     * @return org.springframework.util.MultiValueMap<java.lang.String, org.springframework.http.HttpEntity < ?>>
     * @Description to handle form-data for translations & transcriptions in a method, if you don't like reflex, you can choose other way to achieve it
     * @Param [file, params]
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

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.embedding.EmbeddingResponse>
     * @Description Get a vector representation of a given input that can be easily consumed by machine learning models and algorithms.
     * @Param [embedding]
     **/
    public Mono<EmbeddingResponse> embeddings(Embedding embedding) {
        return webClient.post()
                .uri("/v1/embeddings")
                .bodyValue(embedding)
                .retrieve()
                .bodyToMono(EmbeddingResponse.class);
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.embedding.EmbeddingResponse>
     * @Description Get a vector representation of a given input that can be easily consumed by machine learning models and algorithms.
     * @Param [input]
     **/
    public Mono<EmbeddingResponse> embeddings(String input) {
        Embedding embedding = Embedding.builder().input(List.of(input)).build();
        return this.embeddings(embedding);
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.embedding.EmbeddingResponse>
     * @Description Get a vector representation of a given input that can be easily consumed by machine learning models and algorithms.
     * @Param [input]
     **/
    public Mono<EmbeddingResponse> embeddings(List<String> input) {
        Embedding embedding = Embedding.builder().input(input).build();
        return this.embeddings(embedding);
    }

    /**
     * @return com.hong.chatgpt.entity.embedding.EmbeddingResponse
     * @Description get EmbeddingResponse with block not Mono<EmbeddingResponse>
     * @Param [embedding]
     **/
    public EmbeddingResponse embeddingsBlocking(Embedding embedding) {
        return embeddings(embedding).block();
    }

    /**
     * @return com.hong.chatgpt.entity.embedding.EmbeddingResponse
     * @Description get EmbeddingResponse
     * @Param [input]
     **/
    public EmbeddingResponse embeddingsBlocking(String input) {
        return embeddings(input).block();
    }

    /**
     * @return com.hong.chatgpt.entity.embedding.EmbeddingResponse
     * @Description get EmbeddingResponse with block
     * @Param [input]
     **/
    public EmbeddingResponse embeddingsBlocking(List<String> input) {
        return embeddings(input).block();
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.finetuning.FineTuneResponse>
     * @Description Creates a job that fine-tunes a specified model from a given dataset.
     * @Param [fineTune]
     **/
    public Mono<FineTuneResponse> createFineTuneJobs(FineTune fineTune) {
        return webClient.post()
                .uri("/v1/fine_tuning/jobs")
                .bodyValue(fineTune)
                .retrieve()
                .bodyToMono(FineTuneResponse.class);
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.finetuning.FineTuneResponse>
     * @Description Creates a job that fine-tunes a specified model from a given dataset.
     * @Param [trainingFileId, model]
     **/
    public Mono<FineTuneResponse> createFineTuneJobs(String trainingFileId, String model) {
        FineTune fineTune = FineTune.builder().trainingFile(trainingFileId).model(model).build();
        return webClient.post()
                .uri("/v1/fine_tuning/jobs")
                .bodyValue(fineTune)
                .retrieve()
                .bodyToMono(FineTuneResponse.class);
    }

    /**
     * @return com.hong.chatgpt.entity.finetuning.FineTuneResponse
     * @Description Creates a job that fine-tunes a specified model from a given dataset.
     * @Param [trainingFileId, model]
     **/
    public FineTuneResponse createFineTuneJobsBlocking(String trainingFileId, String model) {
        return createFineTuneJobs(trainingFileId, model).block();
    }

    /**
     * @return com.hong.chatgpt.entity.finetuning.FineTuneResponse
     * @Description Creates a job that fine-tunes a specified model from a given dataset.
     * @Param [fineTune]
     **/
    public FineTuneResponse createFineTuneJobsBlocking(FineTune fineTune) {
        return createFineTuneJobs(fineTune).block();
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.finetuning.FineTuneResponse>
     * @Description List your organization's fine-tuning jobs
     * @Param []
     **/
    public Mono<FineTuneResponse> getFineTuneJobs() {
        return webClient.get()
                .uri("/v1/fine_tuning/jobs")
                .retrieve()
                .bodyToMono(FineTuneResponse.class);
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.finetuning.FineTuneResponse>
     * @Description List your organization's fine-tuning jobs
     * @Param [after, limit]
     **/
    public Mono<FineTuneResponse> getFineTuneJobs(String after, Integer limit) {
        return webClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/v1/fine_tuning/jobs");
                    if (after != null && !after.isEmpty()) builder.queryParam("after", after);
                    if (limit != null) builder.queryParam("limit", limit);
                    return builder.build();
                })
                .retrieve()
                .bodyToMono(FineTuneResponse.class);
    }


    /**
     * @return com.hong.chatgpt.entity.finetuning.FineTuneResponse
     * @Description List your organization's fine-tuning jobs
     * @Param []
     **/
    public FineTuneResponse getFineTuneJobsBlocking() {
        return getFineTuneJobs().block();
    }

    /**
     * @return com.hong.chatgpt.entity.finetuning.FineTuneResponse
     * @Description List your organization's fine-tuning jobs
     * @Param [after, limit]
     **/
    public FineTuneResponse getFineTuneJobsBlocking(String after, Integer limit) {
        return getFineTuneJobs(after, limit).block();
    }

    /**
     * @Description Get status updates for a fine-tuning job.
     * @Param [fineTuningJobId]
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.common.OpenAIResponse<com.hong.chatgpt.entity.finetuning.Event>>
     **/
    public Mono<OpenAIResponse<Event>> getFineTuneJobEvents(String fineTuningJobId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/fine_tuning/jobs/{fine_tuning_job_id}/events")
                        .build(fineTuningJobId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<OpenAIResponse<Event>>() {});
    }

    /**
     * @Description Get status updates for a fine-tuning job.
     * @Param [fineTuningJobId, after, limit]
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.common.OpenAIResponse<com.hong.chatgpt.entity.finetuning.Event>>
     **/
    public Mono<OpenAIResponse<Event>> getFineTuneJobEvents(String fineTuneJobId, String after, Integer limit) {
        return webClient.get()
                .uri(uriBuilder -> {
                    UriBuilder builder = uriBuilder.path("/v1/fine_tuning/jobs/{fine_tuning_job_id}/events");
                    if (after != null && !after.isEmpty()) builder.queryParam("after", after);
                    if (limit != null) builder.queryParam("limit", limit);
                    return builder.build(fineTuneJobId);
                })
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<OpenAIResponse<Event>>() {});
    }

    /**
     * @Description Get status updates for a fine-tuning job.
     * @Param [fineTuningJobId]
     * @return com.hong.chatgpt.entity.common.OpenAIResponse<com.hong.chatgpt.entity.finetuning.Event>
     **/
    public OpenAIResponse<Event> getFineTuneJobEventsBlocking(String fineTuneJobId){
        return getFineTuneJobEvents(fineTuneJobId).block();
    }

    /**
     * @Description Get status updates for a fine-tuning job.
     * @Param [fineTuningJobId, after, limit]
     * @return com.hong.chatgpt.entity.common.OpenAIResponse<com.hong.chatgpt.entity.finetuning.Event>
     **/
    public OpenAIResponse<Event> getFineTuneJobEventsBlocking(String fineTuneJobId, String after, Integer limit){
        return getFineTuneJobEvents(fineTuneJobId, after, limit).block();
    }

    /**
     * @Description Get info about a fine-tuning job.
     * @Param [fineTuningJobId]
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.finetuning.FineTuneResponse>
     **/
    public Mono<FineTuneResponse> retrieveFineTuneJob(String fineTuneJobId){
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/fine_tuning/jobs/{fine_tuning_job_id}")
                        .build(fineTuneJobId))
                .retrieve()
                .bodyToMono(FineTuneResponse.class);
    }

    /**
     * @Description Get info about a fine-tuning job.
     * @Param [fineTuningJobId]
     * @return com.hong.chatgpt.entity.finetuning.FineTuneResponse
     **/
    public FineTuneResponse retrieveFineTuneJobBlocking(String fineTuneJobId){
        return retrieveFineTuneJob(fineTuneJobId).block();
    }

    /**
     * @Description Immediately cancel a fine-tune job.
     * @Param [fineTuneJobId]
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.finetuning.FineTuneResponse>
     **/
    public Mono<FineTuneResponse> cancelFineTune(String fineTuneJobId){
        return webClient.post()
                .uri("/v1/fine_tuning/jobs/{fine_tuning_job_id}/cancel", fineTuneJobId)
                .retrieve()
                .bodyToMono(FineTuneResponse.class);
    }

    /**
     * @Description Immediately cancel a fine-tune job.
     * @Param [fineTuneJobId]
     * @return com.hong.chatgpt.entity.finetuning.FineTuneResponse
     **/
    public FineTuneResponse cancelFineTuneBlocking(String fineTuneJobId){
        return cancelFineTune(fineTuneJobId).block();
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.file.OpenAIFileResponse>
     * @Description POST, upload a file that can be used across various endpoints. A maximum is 512m or 2 million tokens.
     * @Param [purpose, file]
     **/
    public Mono<OpenAIFileResponse> uploadFiles(String purpose, File file) {
        return webClient.post()
                .uri("/v1/files")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createsUploadFile(purpose, file)))
                .retrieve()
                .bodyToMono(OpenAIFileResponse.class);
    }

    /**
     * @return com.hong.chatgpt.entity.file.OpenAIFileResponse
     * @Description Upload a file that can be used across various endpoints. A maximum is 512m or 2 million tokens.
     * @Param [purpose, file]
     **/
    public OpenAIFileResponse uploadFilesBlocking(String purpose, File file) {
        return uploadFiles(purpose, file).block();
    }

    private MultiValueMap<String, HttpEntity<?>> createsUploadFile(String purpose, File file) {
        MultiValueMap<String, HttpEntity<?>> data = new LinkedMultiValueMap<>();
        data.add("purpose", new HttpEntity<>(purpose));
        data.add("file", new HttpEntity<>(new FileSystemResource(file)));
//        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        // 这种方式可能没办法解析上传 .jsonl文件
//        builder.part("file", file).filename(file.getName());
//        builder.part("purpose", purpose);
//        MultiValueMap<String, HttpEntity<?>> multipartBody =builder.build();
        return data;
    }

    /**
     * @return reactor.core.publisher.Mono<java.util.List < com.hong.chatgpt.entity.file.OpenAIFileResponse>>
     * @Description GET, returns a list of files that belong to the user's organization.
     * @Param []
     **/
    public Mono<OpenFilesWrapper> getFileList() {
        return webClient.get()
                .uri("/v1/files")
                .retrieve()
                .bodyToMono(OpenFilesWrapper.class);
    }

    /**
     * @return java.util.List<com.hong.chatgpt.entity.file.OpenAIFileResponse>
     * @Description GET, returns a list of files that belong to the user's organization.
     * @Param []
     **/
    public OpenFilesWrapper getFileListBlocking() {
        return getFileList().block();
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.file.OpenAIFileResponse>
     * @Description GET, returns information about a specific file.
     * @Param [id]
     **/
    public Mono<OpenAIFileResponse> retrieveFile(String id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/files/{file_id}").build(id))
                .retrieve()
                .bodyToMono(OpenAIFileResponse.class);
    }

    /**
     * @return com.hong.chatgpt.entity.file.OpenAIFileResponse
     * @Description GET, returns information about a specific file.
     * @Param [id]
     **/
    public OpenAIFileResponse retrieveFileBlocking(String id) {
        return retrieveFile(id).block();
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.common.DeletedResponse>
     * @Description GET, delete a file.
     * @Param [id]
     **/
    public Mono<DeletedResponse> deleteFile(String id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/files/{file_id}").build(id))
                .retrieve()
                .bodyToMono(DeletedResponse.class);
    }

    /**
     * @return com.hong.chatgpt.entity.common.DeletedResponse
     * @Description GET, delete a file.
     * @Param [id]
     **/
    public DeletedResponse deleteFileBlocking(String id) {
        return deleteFile(id).block();
    }

    /**
     * @return reactor.core.publisher.Mono<org.springframework.web.bind.annotation.ResponseBody>
     * @Description GET, returns the contents of the specified file.
     * @Param [id]
     **/
    public Mono<ResponseBody> retrieveFileContent(String id) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.path("/v1/files/{file_id}/content").build(id))
                .retrieve()
                .bodyToMono(ResponseBody.class);
    }

    /**
     * @return org.springframework.web.bind.annotation.ResponseBody
     * @Description GET, returns the contents of the specified file.
     * @Param [id]
     **/
    public ResponseBody retrieveFileContentBlocking(String id) {
        return retrieveFileContent(id).block();
    }


    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.image.ImageResponse>
     * @Description generate images with DALL·E 3 or DALL·E 2
     * @Param [image]
     **/
    public Mono<ImageResponse> genImages(Image image) {
        return webClient.post()
                .uri("/v1/images/generations")
                .bodyValue(image)
                .retrieve()
                .bodyToMono(ImageResponse.class);
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.image.ImageResponse>
     * @Description generate images with DALL·E 3 or DALL·E 2
     * @Param [prompt]
     **/
    public Mono<ImageResponse> genImages(String prompt) {
        Image image = Image.builder().prompt(prompt).build();
        return genImages(image);
    }

    /**
     * @return com.hong.chatgpt.entity.image.ImageResponse
     * @Description generate images with DALL·E 3 or DALL·E 2
     * @Param [prompt]
     **/
    public ImageResponse genImagesBlocking(String prompt) {
        return genImages(prompt).block();
    }

    /**
     * @return com.hong.chatgpt.entity.image.ImageResponse
     * @Description generate images with DALL·E 3 or DALL·E 2
     * @Param [image]
     **/
    public ImageResponse genImagesBlocking(Image image) {
        return genImages(image).block();
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.image.ImageResponse>
     * @Description edit image with DALL·E 2 only, if mask is not provided, image must have transparency, which will be used as the mask.
     * @Param [image, mask, imageEdit]
     **/
    public Mono<ImageResponse> editImages(File image, File mask, ImageEdit imageEdit) {
        return webClient.post()
                .uri("/v1/images/edits")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createEditImages(image, mask, imageEdit)))
                .retrieve()
                .bodyToMono(ImageResponse.class);
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.image.ImageResponse>
     * @Description edit image with DALL·E 2 only, if mask is not provided, image must have transparency, which will be used as the mask.
     * @Param [image, prompt]
     **/
    public Mono<ImageResponse> editImages(File image, File mask, String prompt) {
        ImageEdit imageEdit = ImageEdit.builder().prompt(prompt).build();
        return editImages(image, mask, imageEdit);
    }

    /**
     * @return com.hong.chatgpt.entity.image.ImageResponse
     * @Description edit image with DALL·E 2 only, if mask is not provided, image must have transparency, which will be used as the mask.
     * @Param [image, imageEdit]
     **/
    public ImageResponse editImagesBlocking(File image, File mask, ImageEdit imageEdit) {
        return editImages(image, mask, imageEdit).block();
    }

    /**
     * @return com.hong.chatgpt.entity.image.ImageResponse
     * @Description edit image with DALL·E 2 only
     * @Param [image, prompt]
     **/
    public ImageResponse editImagesBlocking(File image, File mask, String prompt) {
        return editImages(image, mask, prompt).block();
    }

    /**
     * @return org.springframework.util.MultiValueMap<java.lang.String, org.springframework.http.HttpEntity < ?>>
     * @Description put image, mask & imageEdit's elements into data
     * @Param [image, mask, imageEdit]
     **/
    private MultiValueMap<String, HttpEntity<?>> createEditImages(File image, File mask, ImageEdit imageEdit) {
        MultiValueMap<String, HttpEntity<?>> data = new LinkedMultiValueMap<>();
        checkImage(image);
        getStringHttpEntityMultiValueMap(data, image, imageEdit.getN(), imageEdit.getSize(), imageEdit.getResponseFormat(), imageEdit.getUser());
        data.add("prompt", new HttpEntity<>(imageEdit.getPrompt()));
        if (Objects.nonNull(mask)) data.add("mask", new HttpEntity<>(new FileSystemResource(mask)));
        return data;
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.image.ImageResponse>
     * @Description Creates a variation of a given image with DALL·E 2 only
     * @Param [image, imageVariation]
     **/
    public Mono<ImageResponse> variationImages(File image, ImageVariation imageVariation) {
        return webClient.post()
                .uri("/v1/images/variations")
                .contentType(MediaType.MULTIPART_FORM_DATA)
                .body(BodyInserters.fromMultipartData(createVariationImages(image, imageVariation)))
                .retrieve()
                .bodyToMono(ImageResponse.class);
    }

    /**
     * @return com.hong.chatgpt.entity.image.ImageResponse
     * @Description Creates a variation of a given image with DALL·E 2 only
     * @Param [image]
     **/
    public ImageResponse variationImagesBlocking(File image) {
        return variationImages(image).block();
    }

    /**
     * @return com.hong.chatgpt.entity.image.ImageResponse
     * @Description Creates a variation of a given image with DALL·E 2 only
     * @Param [image, imageVariation]
     **/
    public ImageResponse variationImagesBlocking(File image, ImageVariation imageVariation) {
        return variationImages(image, imageVariation).block();
    }

    /**
     * @return reactor.core.publisher.Mono<com.hong.chatgpt.entity.image.ImageResponse>
     * @Description Creates a variation of a given image with DALL·E 2 only
     * @Param [image]
     **/
    public Mono<ImageResponse> variationImages(File image) {
        ImageVariation imageVariation = ImageVariation.builder().build();
        return variationImages(image, imageVariation);
    }

    /**
     * @return org.springframework.util.MultiValueMap<java.lang.String, org.springframework.http.HttpEntity < ?>>
     * @Description put imageVariation's elements & image into data
     * @Param [image, imageVariation]
     **/
    private MultiValueMap<String, HttpEntity<?>> createVariationImages(File image, ImageVariation imageVariation) {
        MultiValueMap<String, HttpEntity<?>> data = new LinkedMultiValueMap<>();
        checkImage(image);
        getStringHttpEntityMultiValueMap(data, image, imageVariation.getN(), imageVariation.getSize(), imageVariation.getResponseFormat(), imageVariation.getUser());
        data.add("image", new HttpEntity<>(new FileSystemResource(image)));
        return data;
    }

    /**
     * @Description put elements into data
     * @Param [data, image, n, size, responseFormat, user]
     **/
    private void getStringHttpEntityMultiValueMap(MultiValueMap<String, HttpEntity<?>> data, File image, Integer n, String size, String responseFormat, String user) {
        data.add("image", new HttpEntity<>(new FileSystemResource(image)));
        data.add("n", new HttpEntity<>(n.toString()));
        data.add("size", new HttpEntity<>(size));
        data.add("response_format", new HttpEntity<>(responseFormat));
        if (Objects.nonNull(user)) data.add("user", new HttpEntity<>(user));
    }

    private void checkImage(File image) {
        // check isNull
        if (Objects.isNull(image)) {
            logErrorAndThrow("Image cannot be empty!", OpenAIError.PARAMETER_INCORRECT);
        }
        // check format, image is must be a PNG
        String fileName = image.getName().toLowerCase();
        if (!fileName.endsWith(SUPPORTED_FORMAT.toLowerCase())) {
            logErrorAndThrow("Image's format must be PNG or png!", OpenAIError.PARAMETER_INCORRECT);
        }
        // check size, less than 4MB
        if (image.length() > MAX_IMAGE_SIZE) {
            logErrorAndThrow("Image's size must be less than 4MB!", OpenAIError.PARAMETER_INCORRECT);
        }
    }


    private void logErrorAndThrow(String message, OpenAIError error) {
        log.error(message);
        throw new CommonException(error.getOverview(),
                error.getStatusCode(),
                error.getSolution());
    }


}
