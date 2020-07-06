package morse.signal.converters;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;

@AllArgsConstructor
public abstract class StateConverter {
    private final StateConverter next;

    public SignalValue toSignalValue(SignalState signalState) {
        if (next != null) {
            return next.toSignalValue(signalState);
        } else {
            return SignalValue.UNDEFINED;
        }
    }
}
