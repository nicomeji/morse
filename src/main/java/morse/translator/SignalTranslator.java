package morse.translator;

import lombok.AllArgsConstructor;
import morse.models.SignalMeaning;
import morse.models.SignalValue;
import morse.remote.MorseTranslator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.function.Predicate.isEqual;
import static java.util.function.Predicate.not;
import static morse.models.SignalValue.*;

@Service
@AllArgsConstructor
public class SignalTranslator {
    private static final List<SignalValue> EOF = asList(DOT, LINE, DOT, LINE, DOT, LINE);

    private final MorseTranslator translator;
    private final CharMapper charMapper;

    public Flux<SignalMeaning> translate(Flux<SignalValue> signal) {
        return signal.filter(not(isEqual(SPACE)))
                .takeUntil(isEqual(STOP))
                .bufferUntil(isEqual(BREAK))
                .takeUntil(isEqual(EOF))
                .map(this::mapToMorse)
                .flatMap(translator::translate);
    }

    private String mapToMorse(List<SignalValue> signalValues) {
        StringBuilder sb = new StringBuilder();
        signalValues.stream()
                .map(charMapper)
                .forEach(sb::append);
        return sb.toString();
    }
}
