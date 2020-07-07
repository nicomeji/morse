package morse.signal;

import morse.models.SignalValue;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import reactor.core.publisher.Flux;

import java.util.List;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static java.util.function.Predicate.isEqual;
import static java.util.function.Predicate.not;
import static morse.models.SignalValue.*;

@Component
public class SignalSegmentation {
    static final List<SignalValue> EOF = asList(DOT, LINE, DOT, LINE, DOT, LINE);
    public static final List<SignalValue> SEPARATOR = singletonList(WORD_SEPARATOR);

    public Flux<List<SignalValue>> chunk(Flux<SignalValue> signal) {
        return signal.takeUntil(isEqual(SIGNAL_STOP))
                .filter(not(isEqual(SIGNAL_STOP)))
                .filter(not(isEqual(SPACE)))
                .bufferUntil(isEqual(LETTER_SEPARATOR).or(isEqual(WORD_SEPARATOR)))
                .map(this::ignoreLetterSeparator)
                .concatMap(this::separateWordSeparator)
                .takeUntil(isEqual(EOF))
                .filter(not(isEqual(EOF)))
                .filter(not(CollectionUtils::isEmpty));
    }

    private List<SignalValue> ignoreLetterSeparator(List<SignalValue> signal) {
        signal.removeIf(isEqual(LETTER_SEPARATOR));
        return signal;
    }

    private Flux<List<SignalValue>> separateWordSeparator(List<SignalValue> signal) {
        final int index = signal.indexOf(WORD_SEPARATOR);
        if (index < 0) {
            return Flux.just(signal);
        } else {
            return Flux.create(sink -> {
                sink.next(signal.subList(0, index));
                sink.next(SEPARATOR);
                sink.complete();
            });
        }
    }
}
