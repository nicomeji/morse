package morse.signal.scanners;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.utils.mappers.FluxScanner.Scanner;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Cannot assume anything. In case complete is called, it cannot determinate any SignalValue.
 *
 * A single pulse is not enough to assume anything.
 * A second SignalState means that
 */
@AllArgsConstructor
class UndeterminedStateValueScanner implements Scanner<SignalState, SignalValue> {
    static final int MAX_SAMPLES_QTY = 3;

    private final List<SignalState> initialStates = new LinkedList<>();
    private final StateValueScanner context;
    private final StateValueScannerFactory scannerFactory;

    @Override
    public void map(SignalState element, Consumer<SignalValue> next) {
        initialStates.add(element);
        if (initialStates.size() > MAX_SAMPLES_QTY) {
            context.setDelegate(scannerFactory.unstable(context, initialStates));
        }
    }

    @Override
    public void complete(Consumer<SignalValue> next) {
        initialStates.forEach(s -> next.accept(SignalValue.UNDEFINED));
    }
}
