package morse.translator;

import lombok.AllArgsConstructor;
import morse.models.SignalMeaning;
import morse.models.SignalValue;
import morse.remote.MorseTranslator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class SignalTranslator {
    private final MorseTranslator translator;

    public Flux<SignalMeaning> translate(Flux<SignalValue> signal) {
        return signal.bufferUntil(SignalValue.BREAK::equals)
                .map(translator::translate)
                .takeUntil(SignalMeaning.EOF::equals);
    }
}
