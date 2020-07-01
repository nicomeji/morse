package morse.mappers;

import morse.models.SignalState;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class RequestToSignal {
    public Flux<SignalState.State> map(List<Integer> morseMessage) {
        return Flux.fromIterable(morseMessage)
                .map(i -> i == 0 ? SignalState.State.DOWN : SignalState.State.UP);
    }
}
