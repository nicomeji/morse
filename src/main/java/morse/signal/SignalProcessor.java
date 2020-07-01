package morse.signal;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalState.State;
import morse.models.SignalValue;
import morse.utils.mappers.FluxScanner;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@AllArgsConstructor
public class SignalProcessor {
    private final FluxScanner<SignalState, SignalValue> mapper;

    public Flux<SignalValue> process(Flux<State> signal) {
        return mapper.apply(
                signal.bufferUntilChanged()
                        .map(unchanged -> new SignalState(unchanged.get(0), unchanged.size())));
    }
}
