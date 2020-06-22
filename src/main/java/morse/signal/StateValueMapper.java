package morse.signal;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.mapper.UndeterminedStateValueMapper;
import morse.utils.mapper.StatefulFluxMapper.StatefulMapper;

import java.util.function.Consumer;

public class StateValueMapper implements StatefulMapper<SignalState, SignalValue> {
    private StatefulMapper<SignalState, SignalValue> delegate = new UndeterminedStateValueMapper(this);

    @Override
    public void map(SignalState element, Consumer<SignalValue> next) {
        delegate.map(element, next);
    }

    @Override
    public void complete(Consumer<SignalValue> next) {
        delegate.complete(next);
    }

    public void changeDelegate(StatefulMapper<SignalState, SignalValue> delegate) {
        this.delegate = delegate;
    }
}
