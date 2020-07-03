package morse.remote;

import lombok.AllArgsConstructor;
import morse.models.SignalMeaning;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@AllArgsConstructor
public class MorseConnector {
    private final WebClient webClient;

    Mono<SignalMeaning> requestTranslation(final String morseCode) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder.queryParam("code", morseCode).build())
                .exchange()
                .flatMap(response -> response.bodyToMono(Character.class))
                .map(c -> new SignalMeaning(morseCode, c));
    }
}
