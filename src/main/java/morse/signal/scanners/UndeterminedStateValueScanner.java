package morse.signal.scanners;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.StateValueMapper;
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
public class UndeterminedStateValueScanner implements Scanner<SignalState, SignalValue> {
    private final List<SignalState> initialStates;
    private final StateValueMapper context;

    public UndeterminedStateValueScanner(StateValueMapper context) {
        this.initialStates = new LinkedList<>();
        this.context = context;
    }

    @Override
    public void map(SignalState element, Consumer<SignalValue> next) {
        initialStates.add(element);
        if (initialStates.size() == 3) {
            context.changeDelegate(new UnstableStateValueScanner(context, initialStates, null));
        }
    }

    @Override
    public void complete(Consumer<SignalValue> next) {
        next.accept(SignalValue.UNDEFINED);
    }
}
