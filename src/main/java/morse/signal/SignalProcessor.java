package morse.signal;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalState.State;
import morse.models.SignalValue;
import morse.utils.mapper.FluxScanner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class SignalProcessor {
    public final FluxScanner<SignalState, SignalValue> mapper;

    public Flux<SignalValue> process(Flux<Integer> signal) {
        return mapper.apply(
                signal.bufferUntilChanged()
                        .map(unchanged -> new SignalState(mapState(unchanged.get(0)), unchanged.size())));
    }

    private State mapState(int value) {
        return value == 0 ? State.DOWN : State.UP;
    }
}
