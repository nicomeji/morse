package morse.remote;

import lombok.AllArgsConstructor;
import morse.models.SignalMeaning;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@AllArgsConstructor
public class MorseConnector {
    private final WebClient webClient;

    Mono<SignalMeaning> requestTranslation(String morseCode) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("code", morseCode).build())
                .exchange()
                .flatMap(response -> toSignalMeaning(response, morseCode));
    }

    Mono<List<SignalMeaning>> requestTranslation(List<String> codes) {
        return Flux.fromIterable(codes)
                .flatMap(this::requestTranslation)
                .log()
                .collectList();
    }

    private Mono<SignalMeaning> toSignalMeaning(ClientResponse response, String morseCode) {
        if (response.statusCode().is2xxSuccessful()) {
            return response.bodyToMono(String.class)
                    .map(s -> new SignalMeaning(morseCode, s.charAt(0)));
        } else {
            return Mono.just(new SignalMeaning(morseCode, '#'));
        }
    }
}
