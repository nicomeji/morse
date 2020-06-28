package morse.signal;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.mapper.UndeterminedStateValueMapper;
import morse.utils.mappers.FluxScanner.Scanner;

import java.util.function.Consumer;

public class StateValueMapper implements Scanner<SignalState, SignalValue> {
    private Scanner<SignalState, SignalValue> delegate = new UndeterminedStateValueMapper(this);

    @Override
    public void map(SignalState element, Consumer<SignalValue> next) {
        delegate.map(element, next);
    }

    @Override
    public void complete(Consumer<SignalValue> next) {
        delegate.complete(next);
    }

    public void changeDelegate(Scanner<SignalState, SignalValue> delegate) {
        this.delegate = delegate;
    }
}
