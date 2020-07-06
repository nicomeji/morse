package morse.translator;

import lombok.AllArgsConstructor;
import morse.models.SignalMeaning;
import morse.models.SignalValue;
import morse.remote.MorseTranslator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
@AllArgsConstructor
public class SignalTranslator {
    public static final SignalMeaning SEPARATOR = new SignalMeaning("", ' ');
    private final MorseTranslator translator;
    private final SignalSegmentation segmentation;

    public Flux<SignalMeaning> translate(Flux<SignalValue> signal) {
        return segmentation.chunk(signal)
                .concatMap(this::toSignalMeaning);
    }

    private Mono<SignalMeaning> toSignalMeaning(List<SignalValue> signalValues) {
        if (SignalSegmentation.SEPARATOR.equals(signalValues)) {
            return Mono.just(SEPARATOR);
        } else {
            return translator.translate(mapToMorse(signalValues));
        }
    }

    private String mapToMorse(List<SignalValue> signalValues) {
        StringBuilder sb = new StringBuilder();
        signalValues.stream()
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
