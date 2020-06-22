package morse.signal.mapper;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.StateValueMapper;
import morse.utils.mapper.StatefulFluxMapper.StatefulMapper;
import morse.utils.statistics.Mean;

import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;

/**
 * There are enough samples to determinate some values, but with high error rate.
 * In case complete is called, it can push SignalValues.
 *
 * Here I am using "Jenks Natural Breaks" algorithm.
 */
public class UnstableStateValueMapper implements StatefulMapper<SignalState, SignalValue> {
    private static final int MAX_SAMPLES = 50;

    private final List<SignalState> buffer;
    private final StateValueMapper context;
    private final Mean mean;

    UnstableStateValueMapper(
            StateValueMapper context,
            SignalState first,
            SignalState... states) {
        this.context = context;
        this.buffer = new LinkedList<>();
        this.mean = new Mean(first.getDuration());
        for(var state : states) {
            add(state);
        }
    }

    @Override
    public void map(SignalState state, Consumer<SignalValue> next) {
        if (buffer.size() < MAX_SAMPLES) {
            add(state);
        } else {

        }
    }

    @Override
    public void complete(Consumer<SignalValue> next) {

    }

    private void add(SignalState state) {
        buffer.add(state);
        mean.add(state.getDuration());
    }
}
