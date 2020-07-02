package morse.signal.scanners;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.utils.mappers.FluxScanner.Scanner;
import morse.utils.statistics.Range;

import java.util.LinkedList;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Collections.emptyMap;

/**
 * It can push SignalValue in deterministic way with low level of error.
 * In case complete is called, it won't push any SignalValue (everything was already pushed in "map").
 */
@AllArgsConstructor
class StableStateValueScanner implements Scanner<SignalState, SignalValue> {
    static final int BUFFER_CAPACITY = 50;

    private final LinkedList<SignalState> queue = new LinkedList<>();
    private final StateValueScanner context;
    private final Map<SignalState.State, Map<Range<Integer>, SignalValue>> ranges;
    private final StateValueScannerFactory scannerFactory;

    @Override
    public void map(SignalState signalState, Consumer<SignalValue> next) {
        buffer(signalState);
        SignalValue value = ranges.getOrDefault(signalState.getState(), emptyMap())
                .entrySet()
                .stream()
                .filter(entry -> entry.getKey().contains(signalState.getDuration()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(SignalValue.UNDEFINED);
        if (!SignalValue.UNDEFINED.equals(value)) {
            next.accept(value);
        } else {
            context.setDelegate(scannerFactory.unstable(context, queue));
        }
    }

    @Override
    public void complete(Consumer<SignalValue> next) {
        // Do nothing.
    }

    private void buffer(SignalState sample) {
        queue.add(sample);
        if (queue.size() > BUFFER_CAPACITY) {
            queue.removeFirst();
        }
    }
}
