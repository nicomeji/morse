package morse.signal.mapper;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.utils.mapper.FluxScanner.Scanner;
import morse.utils.tuples.Range;
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
public class StableStateValueMapper implements Scanner<SignalState, SignalValue> {
    private final Map<SignalState.State, List<Tuple2<Range<Integer>, SignalValue>>> ranges;

    @Override
    public void map(SignalState signalState, Consumer<SignalValue> next) {
        next.accept(ranges.getOrDefault(signalState.getState(), emptyList())
                .stream()
                .filter(tuple -> tuple.getT1().contains(signalState.getDuration()))
                .map(Tuple2::getT2)
                .findFirst()
                .orElse(SignalValue.UNDEFINED));
    }

    @Override
    public void complete(Consumer<SignalValue> next) {
    }
}
