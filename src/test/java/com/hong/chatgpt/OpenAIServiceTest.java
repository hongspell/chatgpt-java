package com.hong.chatgpt;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hong.chatgpt.entity.audio.Translation;
import com.hong.chatgpt.entity.chat.ChatCompletion;
import com.hong.chatgpt.entity.chat.ChatCompletionResponse;
import com.hong.chatgpt.entity.chat.ChatMessage;
import com.hong.chatgpt.entity.audio.Transcription;
import com.hong.chatgpt.entity.audio.Whisper;
import com.hong.chatgpt.entity.audio.WhisperResponse;
import com.hong.chatgpt.entity.common.DeletedResponse;
import com.hong.chatgpt.entity.common.OpenAIResponse;
import com.hong.chatgpt.entity.embedding.Embedding;
import com.hong.chatgpt.entity.embedding.EmbeddingResponse;
import com.hong.chatgpt.entity.file.OpenAIFileResponse;
import com.hong.chatgpt.entity.file.OpenFilesWrapper;
import com.hong.chatgpt.entity.finetuning.Event;
import com.hong.chatgpt.entity.finetuning.FineTune;
import com.hong.chatgpt.entity.finetuning.FineTuneResponse;
import com.hong.chatgpt.entity.image.*;
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
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.transport.ProxyProvider;

import java.io.File;
import java.util.Arrays;
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

        // default max size is 262144(0.2m)，chang it to 3m, it is for generate image
        int maxSizeInBytes = 16 * 1024 * 1024;

        WebClient.Builder webClientBuilder = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(openAIHost)
                .defaultHeader("Authorization", "Bearer " + apiKey)
                .codecs(clientCodecConfigurer ->
                        clientCodecConfigurer.defaultCodecs().maxInMemorySize(maxSizeInBytes)
                )
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

    @Test
    public void genImages(){
        // if you use b64_json not url, please consider the max buffer size
        Image image = Image.builder().prompt("tiger").responseFormat(ResponseFormat.URL.getName()).build();
        ImageResponse response = service.genImagesBlocking(image);
        System.out.println(response);
    }

    @Test
    public void editImage(){
        ImageResponse response = service.editImagesBlocking(new File("C:\\Users\\three\\Pictures\\avatar.png"), null, "remove sunglasses");
        System.out.println(response);
    }

    @Test
    public void variationImage(){
        ImageResponse response = service.variationImages(new File("C:\\Users\\three\\Pictures\\avatar.png")).block();
        System.out.println(response);
    }

    @Test
    public void getFileList(){
        OpenFilesWrapper fileList = service.getFileListBlocking();
        System.out.println(fileList);
    }

    @Test
    public void uploadFile(){
        OpenAIFileResponse response = service.uploadFilesBlocking("fine-tune", new File("C:\\Users\\three\\Desktop\\test02.jsonl"));
        System.out.println(response);
    }

    @Test
    public void retrieveFile(){
        OpenAIFileResponse response = service.retrieveFileBlocking("file-dqdG93DgeWPaSicHsXn3ZPoR");
        System.out.println(response);
    }

    @Test
    public void deleteFile(){
        DeletedResponse response = service.deleteFileBlocking("file-azHsA0uLGbxhVAih9xpgPPcN");
        System.out.println(response);
    }

    @Test
    public void retrieveFileContent(){
        ResponseBody response = service.retrieveFileContentBlocking("file-dqdG93DgeWPaSicHsXn3ZPoR");
        System.out.println(response);
    }

    @Test
    public void embeddingsForListInput(){
        Embedding build = Embedding.builder().input(Arrays.asList("Test embeddings", "Creates an embedding vector")).build();
        EmbeddingResponse embeddings = service.embeddings(build).block();
        System.out.println(embeddings);
    }

    @Test
    public void embeddingsForStringInput(){
        Embedding build = Embedding.builder().input(List.of("Test embeddings for string input")).build();
        EmbeddingResponse embeddings = service.embeddings(build).block();
        System.out.println(embeddings);
    }

    @Test
    public void createFineTuneJobs(){
        FineTune build = FineTune.builder()
                .trainingFile("file-7EhUQtImW5jxCNs9O3Sq2FHS")
                .model(FineTune.Model.BABBAGE.getName())
                .suffix("aaaaaa")
                .build();
        FineTuneResponse response = service.createFineTuneJobsBlocking(build);
        System.out.println(response);
        // FineTuneResponse(object=fine_tuning.job, id=ftjob-68FbcEWWf1dphfy21u6k1pPK, model=babbage-002,
        // createdAt=1704186886, finishedAt=0, fineTunedModel=null, organizationId=org-wbpTo2MSYYUcPDljr4KIbGVv,
        // resultFiles=[], status=validating_files, validationFiles=null, trainingFiles=file-7EhUQtImW5jxCNs9O3Sq2FHS,
        // hyperparameters=Hyperparameter(batchSize=auto, learningRateMultiplier=auto, nEpochs=auto), trainedTokens=null)
    }

    @Test
    public void getFineTuneJobs(){
        FineTuneResponse response = service.getFineTuneJobsBlocking();
        System.out.println(response);
    }

    @Test
    public void getFineTuneJobsByParams(){
        FineTuneResponse response = service.getFineTuneJobsBlocking(null, 2);
        System.out.println(response);
    }

    @Test
    public void getFineTuneJobsEvent(){
        OpenAIResponse<Event> block = service.getFineTuneJobEvents("ftjob-68FbcEWWf1dphfy21u6k1pPK").block();
        System.out.println(block);
    }

    @Test
    public void getFineTuneJobsEventByParams(){
        OpenAIResponse<Event> block = service.getFineTuneJobEventsBlocking(
                "ftjob-68FbcEWWf1dphfy21u6k1pPK", null, 10);
        System.out.println(block);
    }

    @Test
    public void retrieveFineTuningJob(){
        FineTuneResponse response = service.retrieveFineTuneJob("ftjob-68FbcEWWf1dphfy21u6k1pPK").block();
        System.out.println(response);
    }

    @Test
    public void cancelFineTuningJob(){
        FineTuneResponse response = service.cancelFineTune("ftjob-68FbcEWWf1dphfy21u6k1pPK").block();
        System.out.println(response);
    }

}
