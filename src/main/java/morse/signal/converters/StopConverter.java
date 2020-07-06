package morse.signal.converters;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.utils.statistics.Range;

class StopConverter extends StateConverter {
    private Range<Integer> range;

    StopConverter(StateConverter next, Range<Integer> range) {
        super(next);
        this.range = range;
    }

    @Override
    public SignalValue toSignalValue(SignalState signalState) {
        if (range.getTo() * 10 < signalState.getDuration()) {
            return SignalValue.STOP;
        } else {
            return super.toSignalValue(signalState);
        }
    }
}
