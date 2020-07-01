package morse.service;

import lombok.AllArgsConstructor;
import morse.models.SignalMeaning;
import morse.models.SignalState;
import morse.signal.SignalProcessor;
import morse.translator.SignalTranslator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class MorseService {
    private final SignalProcessor processor;
    private final SignalTranslator translator;

    public Flux<SignalMeaning> decode(Flux<SignalState.State> signal) {
        return translator.translate(processor.process(signal));
    }
}
