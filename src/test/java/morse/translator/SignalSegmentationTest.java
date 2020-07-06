package morse.translator;

import morse.models.SignalValue;

import static morse.models.SignalValue.*;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static java.util.Collections.singletonList;

import static java.util.Arrays.*;

public class SignalSegmentationTest {
    private final SignalSegmentation segmentation = new SignalSegmentation();

    @Test
    public void spacesAreIgnored() {
        final Flux<SignalValue> signal = Flux.just(SPACE, DOT, SPACE, DOT, SPACE, DOT, SPACE);

        StepVerifier.create(segmentation.chunk(signal))
                .expectNext(asList(DOT, DOT, DOT))
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }

    @Test
    public void fluxCompletesOnStopSignal() {
        final Flux<SignalValue> signal = Flux.just(SPACE, DOT, SPACE, DOT, STOP, DOT, SPACE);

        StepVerifier.create(segmentation.chunk(signal))
                .expectNext(asList(DOT, DOT))
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }

    @Test
    public void divideSignalOnEachBreak() {
        final Flux<SignalValue> signal = Flux.just(
                BREAK, SPACE, DOT, SPACE, DOT, SPACE, DOT, BREAK,
                LINE, SPACE, LINE, SPACE, LINE, BREAK,
                DOT, SPACE, DOT, SPACE, DOT, BREAK);

        StepVerifier.create(segmentation.chunk(signal))
                .expectNext(asList(DOT, DOT, DOT))
                .expectNext(asList(LINE, LINE, LINE))
                .expectNext(asList(DOT, DOT, DOT))
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }

    @Test
    public void longSpacesAreProcessSeparately() {
        final Flux<SignalValue> signal = Flux.just(
                LONG_SPACE, DOT, SPACE, DOT, SPACE, DOT, BREAK,
                LINE, SPACE, LINE, SPACE, LINE, LONG_SPACE,
                DOT, SPACE, DOT, SPACE, DOT, BREAK);

        StepVerifier.create(segmentation.chunk(signal))
                .expectNext(singletonList(LONG_SPACE))
                .expectNext(asList(DOT, DOT, DOT))
                .expectNext(asList(LINE, LINE, LINE))
                .expectNext(singletonList(LONG_SPACE))
                .expectNext(asList(DOT, DOT, DOT))
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }

    @Test
    public void fluxCompleteIfEOFIsReached() {
        final Flux<SignalValue> signal = Flux.just(
                DOT, SPACE, DOT, SPACE, DOT, BREAK,
                LINE, SPACE, LINE, SPACE, LINE, LONG_SPACE,
                DOT, SPACE, DOT, SPACE, DOT, BREAK,
                DOT, SPACE, LINE, SPACE, DOT, SPACE, LINE, SPACE, DOT, SPACE, LINE, BREAK,
                DOT, SPACE, DOT, SPACE, DOT, BREAK);

        StepVerifier.create(segmentation.chunk(signal))
                .expectNext(asList(DOT, DOT, DOT))
                .expectNext(asList(LINE, LINE, LINE))
                .expectNext(singletonList(LONG_SPACE))
                .expectNext(asList(DOT, DOT, DOT))
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }
}
