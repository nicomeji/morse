package morse.signal.scanners;

import lombok.EqualsAndHashCode;
import lombok.ToString;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.utils.scanners.FluxScanner.Scanner;

import java.util.function.Consumer;

@ToString
@EqualsAndHashCode
class StateValueScanner implements Scanner<SignalState, SignalValue> {
    private Scanner<SignalState, SignalValue> delegate;

    @Override
    public void map(SignalState element, Consumer<SignalValue> next) {
        delegate.map(element, next);
    }

    @Override
    public void complete(Consumer<SignalValue> next) {
        delegate.complete(next);
    }

    void setDelegate(Scanner<SignalState, SignalValue> delegate) {
        this.delegate = delegate;
    }
}
