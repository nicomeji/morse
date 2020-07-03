package morse.signal.scanners;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.clustering.JenksNaturalBreaksClustering;
import morse.utils.mappers.FluxScanner.Scanner;
import morse.utils.statistics.Range;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static morse.models.SignalValue.*;

/**
 * There are enough samples to determinate some values, but with high error rate.
 * In case complete is called, it can push SignalValues.
 * <p>
 * Here I am using "Jenks Natural Breaks" algorithm.
 */
@AllArgsConstructor
class UnstableStateValueScanner implements Scanner<SignalState, SignalValue> {
    static final int MAX_SAMPLES_QTY = 50;

    private final StateValueScanner context;
    private final List<SignalState> buffer;
    private final JenksNaturalBreaksClustering clustering;
    private final StateValueScannerFactory scannerFactory;

    @Override
    public void map(SignalState state, Consumer<SignalValue> next) {
        buffer.add(state);
        if (buffer.size() > MAX_SAMPLES_QTY) {
            context.setDelegate(process(next));
        }
    }

    @Override
    public void complete(Consumer<SignalValue> next) {
        process(next).complete(next);
    }

    private Scanner<SignalState, SignalValue> process(Consumer<SignalValue> next) {
        final Scanner<SignalState, SignalValue> stable = scannerFactory.stable(context, ranges());
        buffer.forEach(s -> stable.map(s, next));
        return stable;
    }

    private Map<SignalState.State, Map<Range<Integer>, SignalValue>> ranges() {
        final List<Range<Integer>> clusters = clustering.getClusters(
                buffer.stream()
                        .map(SignalState::getDuration)
                        .collect(Collectors.toList()));

        final Map<Range<Integer>, SignalValue> upValueMap = Map.of(
                clusters.get(0), DOT,
                clusters.get(1), LINE);

        final Map<Range<Integer>, SignalValue> downValueMap = Map.of(
                clusters.get(0), SPACE,
                clusters.get(1), BREAK);

        return Map.of(
                SignalState.State.UP, upValueMap,
                SignalState.State.DOWN, downValueMap);
    }
}
