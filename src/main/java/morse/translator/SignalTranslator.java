package morse.translator;

import lombok.AllArgsConstructor;
import morse.models.SignalMeaning;
import morse.models.SignalValue;
import morse.remote.MorseTranslator;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

import static java.util.function.Predicate.isEqual;
import static java.util.function.Predicate.not;
import static morse.models.SignalValue.*;

@Service
@AllArgsConstructor
public class SignalTranslator {
    public static final String EOF = ".-.-.-";

    private final MorseTranslator translator;

    public Flux<SignalMeaning> translate(Flux<SignalValue> signal) {
        return signal.filter(not(isEqual(SPACE)))
                .takeUntil(isEqual(STOP))
                .filter(not(isEqual(STOP)))
                .bufferUntil(isEqual(BREAK).or(isEqual(LONG_SPACE)))
                .concatMap(this::map)
                .takeUntil(meaning -> EOF.equals(meaning.getMorse()))
                .filter(not(meaning -> EOF.equals(meaning.getMorse())));
    }

    private Publisher<SignalMeaning> map(List<SignalValue> signal) {
        final String morseCode = mapToMorse(signal);
        if (!EOF.equals(morseCode)) {
            Mono<SignalMeaning> translated = translator.translate(morseCode);
            if (signal.contains(LONG_SPACE)) {
                return translated.concatWith(Mono.just(new SignalMeaning(null, ' ')));
            } else {
                return translated;
            }
        } else {
            return Mono.just(new SignalMeaning(EOF, ' '));
        }
    }

    private String mapToMorse(List<SignalValue> signalValues) {
        StringBuilder sb = new StringBuilder();
        signalValues.stream()
                .filter(s -> !BREAK.equals(s) && !LONG_SPACE.equals(s))
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
