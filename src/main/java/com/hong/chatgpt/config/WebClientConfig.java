package com.hong.chatgpt.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;
import reactor.netty.tcp.TcpClient;
import reactor.netty.transport.ProxyProvider;

import java.time.Duration;

/**
 * @Author hong
 * @Description //TODO
 * @Date 12/25/2023
 **/
@Configuration
public class WebClientConfig {

    @Value("${openAIHost}")
    private String openAIHost;

    @Value("${secretKey}")
    private String apiKey;

    @Bean
    public WebClient.Builder builder() {

          // create proxy, support to access https://api.openai.com/
        HttpClient httpClient = HttpClient.create()
                .proxy(proxy -> proxy.type(ProxyProvider.Proxy.HTTP)
                        .host("127.0.0.1")
                        .port(7890))
                .responseTimeout(Duration.ofSeconds(30));

        // 然后，把下面函数加载WebClient.builder()下面.baseUrl(openAIHost)上面
        // .clientConnector(new ReactorClientHttpConnector(httpClient))


        // return WebClient.Builder instance
        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(openAIHost)
                .defaultHeader("Authorization", "Bearer " + apiKey);
    }

}
