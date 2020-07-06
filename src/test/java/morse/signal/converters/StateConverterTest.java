package morse.signal.converters;

import morse.models.SignalState;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class StateConverterTest {
    // TODO - Complete this test:

    private List<SignalState> generateSignal(int length) {
        return IntStream.range(0, length)
                .mapToObj(i -> {
                    SignalState.State state = i % 2 == 0 ? SignalState.State.UP : SignalState.State.DOWN;
                    int duration = i % 4 < 2 ? 40 : 120;
                    return new SignalState(state, duration);
                }).collect(Collectors.toList());
    }
}
