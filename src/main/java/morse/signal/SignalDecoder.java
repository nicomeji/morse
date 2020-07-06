package morse.signal;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalState.State;
import morse.models.SignalValue;
import morse.utils.mappers.FluxScanner;
import reactor.core.publisher.Flux;

import java.util.List;

@AllArgsConstructor
public class SignalDecoder {
    private final SignalSegmentation segmentation;
    private final FluxScanner<SignalState, SignalValue> scanner;

    public Flux<List<SignalValue>> decodeBits2Morse(Flux<State> signal) {
        return segmentation.chunk(
                scanner.apply(
                        signal.bufferUntilChanged()
                                .map(s -> new SignalState(s.get(0), s.size()))));
    }
}
