package morse.signal.converters;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import org.springframework.lang.Nullable;

@AllArgsConstructor
public abstract class StateConverter {
    @Nullable
    private final StateConverter next;

    public SignalValue toSignalValue(SignalState signalState) {
        if (next != null) {
            return next.toSignalValue(signalState);
        } else {
            return SignalValue.UNDEFINED;
        }
    }
}
