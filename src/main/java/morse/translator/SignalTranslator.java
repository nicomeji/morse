package morse.translator;

import lombok.AllArgsConstructor;
import morse.models.SignalMeaning;
import morse.models.SignalValue;
import morse.remote.MorseTranslator;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

import static morse.models.SignalValue.BREAK;
import static morse.models.SignalValue.LONG_SPACE;

@Service
@AllArgsConstructor
public class SignalTranslator {
    private final MorseTranslator translator;
    private final SignalSegmentation segmentation;

    public Flux<SignalMeaning> translate(Flux<SignalValue> signal) {
        return segmentation.chunk(signal)
                .map(this::mapToMorse)
                .concatMap(translator::translate);
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
