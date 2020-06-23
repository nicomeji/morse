package morse.signal.mapper;

import lombok.RequiredArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.StateValueMapper;
import morse.utils.mapper.StatefulFluxMapper.StatefulMapper;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Cannot assume anything. In case complete is called, it cannot determinate any SignalValue.
 *
 * A single pulse is not enough to assume anything.
 * A second SignalState means that
 */
@RequiredArgsConstructor
public class UndeterminedStateValueMapper implements StatefulMapper<SignalState, SignalValue> {
    private final List<SignalState> initialStates = new LinkedList<>();
    private final StateValueMapper context;

    @Override
    public void map(SignalState element, Consumer<SignalValue> next) {
        initialStates.add(element);
        if (initialStates.size() == UnstableStateValueMapper.MIN_SAMPLE_QTY) {
            context.changeDelegate(new UnstableStateValueMapper(context, initialStates));
        }
    }

    @Override
    public void complete(Consumer<SignalValue> next) {
        next.accept(SignalValue.UNDEFINED);
    }
}
