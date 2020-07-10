package morse.signal.scanners;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.converters.StateConverter;
import morse.utils.scanners.FluxScanner.Scanner;

import java.util.LinkedList;
import java.util.function.Consumer;

import static java.util.Objects.requireNonNull;

/**
 * It can push SignalValue in deterministic way with low level of error.
 * In case complete is called, it won't push any SignalValue (everything was already pushed in "map").
 */
@AllArgsConstructor
class StableStateValueScanner implements Scanner<SignalState, SignalValue> {
    static final int BUFFER_CAPACITY = 40;

    private final LinkedList<SignalState> queue = new LinkedList<>();
    private final StateValueScanner context;
    private final StateConverter stateConverter;
    private final StateValueScannerFactory scannerFactory;

    @Override
    public void map(SignalState signalState, Consumer<SignalValue> next) {
        buffer(signalState);
        SignalValue value = requireNonNull(stateConverter.toSignalValue(signalState));
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
