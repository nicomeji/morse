package morse.signal.scanners;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.StateValueMapper;
import morse.utils.collections.CircularQueue;
import morse.utils.mappers.FluxScanner.Scanner;
import morse.utils.statistics.Range;
import reactor.util.function.Tuple2;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.emptyList;

/**
 * It can push SignalValue in deterministic way with low level of error.
 * In case complete is called, it won't push any SignalValue (everything was already pushed in "map").
 */
@AllArgsConstructor
class StableStateValueScanner implements Scanner<SignalState, SignalValue> {
    public static final int BUFFER_CAPACITY = 50;

    private final StateValueMapper context;
    private final Map<SignalState.State, List<Tuple2<Range<Integer>, SignalValue>>> ranges;
    private final CircularQueue<SignalState> buffer = new CircularQueue<>(BUFFER_CAPACITY);

    @Override
    public void map(SignalState signalState, Consumer<SignalValue> next) {
        buffer.add(signalState);
        SignalValue value = ranges.getOrDefault(signalState.getState(), emptyList())
                .stream()
                .filter(tuple -> tuple.getT1().contains(signalState.getDuration()))
                .map(Tuple2::getT2)
                .findFirst()
                .orElse(SignalValue.UNDEFINED);
        if (!SignalValue.UNDEFINED.equals(value)) {
            next.accept(value);
        } else {
            context.changeDelegate(new UnstableStateValueScanner(context, buffer, null));
        }
    }

    @Override
    public void complete(Consumer<SignalValue> next) {
        // Do nothing.
    }
}
