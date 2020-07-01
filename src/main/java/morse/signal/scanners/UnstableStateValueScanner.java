package morse.signal.scanners;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.StateValueMapper;
import morse.signal.clustering.OneDimensionDiscreteClustering;
import morse.utils.mappers.FluxScanner.Scanner;

import java.util.Collection;
import java.util.function.Consumer;

/**
 * There are enough samples to determinate some values, but with high error rate.
 * In case complete is called, it can push SignalValues.
 * <p>
 * Here I am using "Jenks Natural Breaks" algorithm.
 */
@AllArgsConstructor
class UnstableStateValueScanner implements Scanner<SignalState, SignalValue> {
    public static final int MAX_SAMPLES_QTY = 20;

    private final StateValueMapper context;
    private final Collection<SignalState> buffer; // BUG
    private final OneDimensionDiscreteClustering<Void, Integer> clustering;

    @Override
    public void map(SignalState state, Consumer<SignalValue> next) {
        buffer.add(state);
        if (buffer.size() > MAX_SAMPLES_QTY) {
            context.changeDelegate(process(next));
        }
    }

    @Override
    public void complete(Consumer<SignalValue> next) {
        process(next);
    }

    private StableStateValueScanner process(Consumer<SignalValue> next) {
        StableStateValueScanner mapper = new StableStateValueScanner(context, null);
/*        List<Range<Integer>> ranges = clustering.getClusters(
                buffer.stream()
                        .map(SignalState::getDuration)
                        .collect(Collectors.toList())); */
        return null;
    }
}
