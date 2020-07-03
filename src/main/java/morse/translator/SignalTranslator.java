package morse.translator;

import lombok.AllArgsConstructor;
import morse.models.SignalMeaning;
import morse.models.SignalValue;
import morse.remote.MorseTranslator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.function.Predicate.isEqual;
import static java.util.function.Predicate.not;
import static morse.models.SignalValue.*;

@Service
@AllArgsConstructor
public class SignalTranslator {
    private static final String EOF = ".-.-.-";

    private final MorseTranslator translator;

    public Flux<SignalMeaning> translate(Flux<SignalValue> signal) {
        return signal.filter(not(isEqual(SPACE)))
                .takeUntil(isEqual(STOP))
                .filter(not(isEqual(STOP)))
                .bufferUntil(isEqual(BREAK))
                .map(this::mapToMorse)
                .takeUntil(isEqual(EOF))
                .filter(not(isEqual(EOF)))
                .flatMap(translator::translate);
    }

    private String mapToMorse(List<SignalValue> signalValues) {
        StringBuilder sb = new StringBuilder();
        signalValues.stream()
                .filter(s -> !BREAK.equals(s) && !STOP.equals(s))
                .map(this::mapToChar)
                .forEach(sb::append);
        return sb.toString();
    }

    private char mapToChar(SignalValue value) {
        switch (value) {
        case DOT:
            return '.';
        case LINE:
            return '-';
        default:
            throw new IllegalArgumentException();
        }
    }
}
