package morse.signal;

import morse.models.SignalState;
import morse.models.SignalState.State;
import morse.models.SignalValue;
import morse.utils.mappers.FluxScanner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static morse.models.SignalState.State.DOWN;
import static morse.models.SignalState.State.UP;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SignalProcessorTest {
    @Mock
    private FluxScanner<SignalState, SignalValue> mapper;

    @InjectMocks
    private SignalProcessor processor;

    @Test
    public void processor() {
        final Flux<State> signal = Flux.just(DOWN, DOWN, DOWN, UP, UP, DOWN, UP, UP, DOWN);
        final Flux<SignalValue> values = Flux.empty();

        when(mapper.apply(any()))
                .thenAnswer(invocation -> {
                    StepVerifier.create(invocation.getArgument(0))
                            .expectSubscription()
                            .expectNext(new SignalState(DOWN, 3))
                            .expectNext(new SignalState(UP, 2))
                            .expectNext(new SignalState(DOWN, 1))
                            .expectNext(new SignalState(UP, 2))
                            .expectNext(new SignalState(DOWN, 1))
                            .expectComplete()
                            .verify(Duration.ofMillis(10));
                    return values;
                });

        StepVerifier.create(processor.process(signal))
                .expectSubscription()
                .expectComplete()
                .verify(Duration.ofMillis(10));
    }
}
