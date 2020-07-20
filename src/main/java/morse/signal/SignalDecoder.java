package morse.signal;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morse.models.SignalState;
import morse.models.SignalState.State;
import morse.models.SignalValue;
import morse.utils.scanners.FluxScanner;
import org.reactivestreams.Publisher;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Signal;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
@Component
@AllArgsConstructor
public class SignalDecoder {
    private final SignalSegmentation segmentation;
    private final FluxScannerFactory scannerFactory;

    public Flux<List<SignalValue>> decodeBits2Morse(Flux<State> flux) {
        log.info("START DECODING");
        AtomicReference<Boolean> flag = new AtomicReference<>(true);
        FluxScanner<SignalState, SignalValue> scanner = scannerFactory.create();

        final Flux<SignalValue> signal = scanner.apply(groupPulses(flux).switchOnFirst(this::ignoreFirstDown))
                .delayElements(delay(flag));
        final Flux<List<SignalValue>> chunk = segmentation.chunk(signal);
        return chunk;
    }

    private Duration delay(final AtomicReference<Boolean> flag) {
        if (flag.updateAndGet(f -> !f)) {
            log.info("DELAY");
            return Duration.ofMillis(12);
        }
        return Duration.ZERO;
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
