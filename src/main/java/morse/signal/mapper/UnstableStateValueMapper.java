package morse.signal.mapper;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.StateValueMapper;
import morse.utils.mapper.FluxScanner.Scanner;
import morse.utils.statistics.Mean;

import java.util.*;
import java.util.function.Consumer;

import static java.util.Optional.ofNullable;

/**
 * There are enough samples to determinate some values, but with high error rate.
 * In case complete is called, it can push SignalValues.
 * <p>
 * Here I am using "Jenks Natural Breaks" algorithm.
 */
public class UnstableStateValueMapper implements Scanner<SignalState, SignalValue> {
    public static final int MIN_SAMPLE_QTY = 4;
    public static final int MAX_SAMPLES_QTY = 50;

    private final List<SignalState> buffer;
    private final StateValueMapper context;
    private final Map<SignalState.State, Mean> means;

    UnstableStateValueMapper(
            StateValueMapper context,
            Collection<SignalState> states) {
        if (states.size() < MIN_SAMPLE_QTY) {
            throw new IllegalArgumentException();
        }
        this.context = context;
        this.buffer = new LinkedList<>();
        this.means = new EnumMap<>(SignalState.State.class);
        for (var state : states) {
            add(state);
        }
    }

    @Override
    public void map(SignalState state, Consumer<SignalValue> next) {
        if (buffer.size() < MAX_SAMPLES_QTY) {
            add(state);
        } else {

        }
    }

    @Override
    public void complete(Consumer<SignalValue> next) {

    }

    private void add(SignalState signalState) {
        buffer.add(signalState);
        means.put(
                signalState.getState(),
                ofNullable(means.get(signalState.getState()))
                        .map(mean -> mean.add(signalState.getDuration()))
                        .orElseGet(() -> new Mean(signalState.getDuration())));
    }
}
