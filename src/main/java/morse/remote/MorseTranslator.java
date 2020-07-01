package morse.remote;

import lombok.AllArgsConstructor;
import morse.cache.MorseCache;
import morse.models.SignalMeaning;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@AllArgsConstructor
public class MorseTranslator {
    private final MorseCache morseCache;
    private final MorseConnector morseConnector;

    public Mono<SignalMeaning> translate(final String morseCode) {
        final Mono<SignalMeaning> retrieve = morseCache.retrieve(morseCode);
        return retrieve.switchIfEmpty(
                morseConnector.requestTranslation(morseCode)
                        .doOnNext(signalMeaning -> morseCache.save(morseCode, signalMeaning)));
    }
}
