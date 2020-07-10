package morse.signal;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalState.State;
import morse.models.SignalValue;
import morse.utils.scanners.FluxScanner;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Signal;

import java.util.List;

@AllArgsConstructor
public class SignalDecoder {
    private final SignalSegmentation segmentation;
    private final FluxScanner<SignalState, SignalValue> scanner;

    public Flux<List<SignalValue>> decodeBits2Morse(Flux<State> flux) {
        return segmentation.chunk(
                scanner.apply(groupPulses(flux).switchOnFirst(this::ignoreFirstDown)));
    }

    private Flux<SignalState> groupPulses(Flux<State> signal) {
        return signal.bufferUntilChanged()
                .map(s -> new SignalState(s.get(0), s.size()));
    }

    private Publisher<SignalState> ignoreFirstDown(Signal<? extends SignalState> first, Flux<SignalState> signal) {
        if (first.hasValue()) {
            if (State.DOWN.equals(first.get().getState())) {
                return signal.skip(1);
            } else {
                return signal;
            }
        } else {
            return signal;
        }
    }
}
