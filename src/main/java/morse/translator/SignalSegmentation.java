package morse.translator;

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
    public static final List<SignalValue> EOF = asList(DOT, LINE, DOT, LINE, DOT, LINE);

    public Flux<List<SignalValue>> chunk(Flux<SignalValue> signal) {
        return signal.takeUntil(isEqual(STOP))
                .filter(not(isEqual(STOP)))
                .filter(not(isEqual(SPACE)))
                .bufferUntil(isEqual(BREAK).or(isEqual(LONG_SPACE)))
                .map(this::ignoreBreak)
                .concatMap(this::separateLongSpace)
                .takeUntil(isEqual(EOF))
                .filter(not(isEqual(EOF)))
                .filter(not(CollectionUtils::isEmpty));
    }

    private List<SignalValue> ignoreBreak(List<SignalValue> signal) {
        signal.removeIf(isEqual(BREAK));
        return signal;
    }

    private Flux<List<SignalValue>> separateLongSpace(List<SignalValue> signal) {
        final int index = signal.indexOf(LONG_SPACE);
        if (index < 0) {
            return Flux.just(signal);
        } else {
            return Flux.create(sink -> {
                sink.next(signal.subList(0, index));
                sink.next(singletonList(LONG_SPACE));
                sink.complete();
            });
        }
    }
}
