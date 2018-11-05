package com.coinportfolio.coinportfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;

@SpringBootApplication
public class CoinPortfolioApplication {

    public static void main(String[] args) {
        SpringApplication.run(CoinPortfolioApplication.class, args);
    }
}

@RestController
class HelloWorldController {

    @GetMapping(value = "/helloworld", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Mono<String> getText() {
        return Mono.just("Hello world");
    }

    @GetMapping(value = "/ticker", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<String> counter() {
        return Flux.interval(Duration.ofMillis(1000))
                .map(item -> "tick: " + item);
    }
}

@Component
class CorsFilter implements WebFilter {

    @Override
    public Mono<Void> filter(final ServerWebExchange serverWebExchange, final WebFilterChain webFilterChain) {
        // Adapted from https://sandstorm.de/de/blog/post/cors-headers-for-spring-boot-kotlin-webflux-reactor-project.html
        serverWebExchange.getResponse().getHeaders().add("Access-Control-Allow-Origin", "*");
        serverWebExchange.getResponse().getHeaders().add("Access-Control-Allow-Methods",
                "GET, PUT, POST, DELETE, OPTIONS");
        serverWebExchange.getResponse().getHeaders().add("Access-Control-Allow-Headers",
                "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With," +
                        "If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range");
        if (serverWebExchange.getRequest().getMethod() == HttpMethod.OPTIONS) {
            serverWebExchange.getResponse().getHeaders().add("Access-Control-Max-Age", "1728000");
            serverWebExchange.getResponse().setStatusCode(HttpStatus.NO_CONTENT);
            return Mono.empty();
        } else {
            serverWebExchange.getResponse().getHeaders().add("Access-Control-Expose-Headers",
                    "DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With," +
                            "If-Modified-Since,Cache-Control,Content-Type,Content-Range,Range");
            return webFilterChain.filter(serverWebExchange);
        }
    }
}

