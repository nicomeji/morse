package morse.signal.converters;

import morse.models.SignalState;
import morse.models.SignalValue;

class StopConverter extends StateConverter {
    private final int maxDuration;

    StopConverter(StateConverter next, int maxDuration) {
        super(next);
        this.maxDuration = maxDuration;
    }

    @Override
    public SignalValue toSignalValue(SignalState signalState) {
        if (SignalState.State.DOWN.equals(signalState.getState()) &&
                maxDuration < signalState.getDuration()) {
            return SignalValue.SIGNAL_STOP;
        } else {
            return super.toSignalValue(signalState);
        }
    }
}
