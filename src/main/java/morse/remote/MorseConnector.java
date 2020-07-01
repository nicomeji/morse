package morse.remote;

import morse.models.SignalMeaning;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MorseConnector {
    public Mono<SignalMeaning> requestTranslation(final String morseCode) {
        return Mono.empty();
    }
}
