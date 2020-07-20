package morse.signal;

import morse.models.SignalState;
import morse.models.SignalState.State;
import morse.models.SignalValue;
import morse.utils.scanners.FluxScanner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static morse.models.SignalState.State.DOWN;
import static morse.models.SignalState.State.UP;
import static morse.models.SignalValue.DOT;
import static morse.models.SignalValue.SPACE;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SignalDecoderTest {
    @Mock
    private FluxScanner<SignalState, SignalValue> fluxScanner;

    @Mock
    private FluxScannerFactory scannerFactory;

    @Mock
    private SignalSegmentation segmentation;

    @InjectMocks
    private SignalDecoder processor;

    @Test
    public void decodingSignal() {
        final Flux<State> signal = Flux.just(UP, UP, DOWN, UP, UP, DOWN, DOWN, DOWN, UP, UP);
        final Flux<SignalValue> values = Flux.just(DOT, SPACE, DOT, SPACE);
        final List<SignalValue> morse = List.of(DOT, DOT);
        final Flux<List<SignalValue>> morseFlux = Flux.just(morse);

        when(scannerFactory.create()).thenReturn(fluxScanner);
        when(fluxScanner.apply(any()))
                .thenAnswer(invocation -> {
                    StepVerifier.create(invocation.getArgument(0))
                            .expectSubscription()
                            .expectNext(new SignalState(UP, 2))
                            .expectNext(new SignalState(DOWN, 1))
                            .expectNext(new SignalState(UP, 2))
                            .expectNext(new SignalState(DOWN, 3))
                            .expectNext(new SignalState(UP, 2))
                            .expectComplete()
                            .verify(Duration.ofMillis(100));
                    return values;
                });

        when(segmentation.chunk(any()))
                .thenAnswer(invocation -> {
                    StepVerifier.create(invocation.getArgument(0))
                            .expectSubscription()
                            .expectNext(DOT)
                            .expectNext(SPACE)
                            .expectNext(DOT)
                            .expectNext(SPACE)
                            .expectComplete()
                            .verify(Duration.ofMillis(100));
                    return morseFlux;
                });

        StepVerifier.create(processor.decodeBits2Morse(signal))
                .expectSubscription()
                .expectNext(morse)
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }

    @Test
    public void ignoringFirstDown() {
        final Flux<State> signal = Flux.just(DOWN, DOWN, DOWN, UP, UP, DOWN, UP, UP, DOWN);
        final Flux<SignalValue> values = Flux.just(DOT, SPACE, DOT, SPACE);
        final List<SignalValue> morse = List.of(DOT, DOT);
        final Flux<List<SignalValue>> morseFlux = Flux.just(morse);

        when(scannerFactory.create()).thenReturn(fluxScanner);
        when(fluxScanner.apply(any()))
                .thenAnswer(invocation -> {
                    StepVerifier.create(invocation.getArgument(0))
                            .expectSubscription()
                            .expectNext(new SignalState(UP, 2))
                            .expectNext(new SignalState(DOWN, 1))
                            .expectNext(new SignalState(UP, 2))
                            .expectNext(new SignalState(DOWN, 1))
                            .expectComplete()
                            .verify(Duration.ofMillis(100));
                    return values;
                });

        when(segmentation.chunk(any()))
                .thenAnswer(invocation -> {
                    StepVerifier.create(invocation.getArgument(0))
                            .expectSubscription()
                            .expectNext(DOT)
                            .expectNext(SPACE)
                            .expectNext(DOT)
                            .expectNext(SPACE)
                            .expectComplete()
                            .verify(Duration.ofMillis(100));
                    return morseFlux;
                });

        StepVerifier.create(processor.decodeBits2Morse(signal))
                .expectSubscription()
                .expectNext(morse)
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }
}
