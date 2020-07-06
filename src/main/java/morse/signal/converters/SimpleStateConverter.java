package morse.signal.converters;

import morse.models.SignalState;
import morse.models.SignalState.State;
import morse.models.SignalValue;
import morse.utils.statistics.Range;

class SimpleStateConverter extends StateConverter {
    private final Range<Integer> range;
    private final State supported;
    private final SignalValue value;

    SimpleStateConverter(
            StateConverter next,
            Range<Integer> range,
            State supported,
            SignalValue value) {
        super(next);
        this.range = range;
        this.supported = supported;
        this.value = value;
    }

    @Override
    public SignalValue toSignalValue(SignalState signalState) {
        if (range.contains(signalState.getDuration()) &&
                supported.equals(signalState.getState())) {
            return value;
        } else {
            return super.toSignalValue(signalState);
        }
    }
}
