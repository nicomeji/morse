package morse.signal.mapper;

import lombok.RequiredArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.StateValueMapper;
import morse.utils.mapper.StatefulFluxMapper.StatefulMapper;

import java.util.function.Consumer;

/**
 * Cannot assume anything.
 * In case complete is called, it cannot determinate any SignalValue.
 */
@RequiredArgsConstructor
public class UndeterminedStateValueMapper implements StatefulMapper<SignalState, SignalValue> {
    private final StateValueMapper context;
    private SignalState initialState;

    @Override
    public void map(SignalState element, Consumer<SignalValue> next) {
        if (initialState == null) {
            initialState = element;
        } else {
            context.changeDelegate(new UnstableStateValueMapper(context, initialState, element));
        }
    }

    @Override
    public void complete(Consumer<SignalValue> next) {
        next.accept(SignalValue.UNDEFINED);
    }
}
