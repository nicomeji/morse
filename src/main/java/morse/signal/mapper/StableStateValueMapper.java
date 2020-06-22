package morse.signal.mapper;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.StateValueMapper;
import morse.utils.mapper.StatefulFluxMapper.StatefulMapper;

import java.util.function.Consumer;

/**
 * It can push SignalValue in deterministic way with low level of error.
 * In case complete is called, it won't push any SignalValue (everything was already pushed in "map").
 */
@AllArgsConstructor
public class StableStateValueMapper implements StatefulMapper<SignalState, SignalValue> {
    private final StateValueMapper context;

    @Override
    public void map(SignalState element, Consumer<SignalValue> next) {

    }

    @Override
    public void complete(Consumer<SignalValue> next) {

    }
}
