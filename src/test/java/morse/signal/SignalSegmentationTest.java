package morse.signal;

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
        final Flux<SignalValue> signal = Flux.just(SPACE, DOT, SPACE, DOT, SIGNAL_STOP, DOT, SPACE);

        StepVerifier.create(segmentation.chunk(signal))
                .expectNext(asList(DOT, DOT))
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }

    @Test
    public void divideSignalOnEachBreak() {
        final Flux<SignalValue> signal = Flux.just(
                LETTER_SEPARATOR, SPACE, DOT, SPACE, DOT, SPACE, DOT, LETTER_SEPARATOR,
                LINE, SPACE, LINE, SPACE, LINE, LETTER_SEPARATOR,
                DOT, SPACE, DOT, SPACE, DOT, LETTER_SEPARATOR);

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
                WORD_SEPARATOR, DOT, SPACE, DOT, SPACE, DOT, LETTER_SEPARATOR,
                LINE, SPACE, LINE, SPACE, LINE, WORD_SEPARATOR,
                DOT, SPACE, DOT, SPACE, DOT, LETTER_SEPARATOR);

        StepVerifier.create(segmentation.chunk(signal))
                .expectNext(singletonList(WORD_SEPARATOR))
                .expectNext(asList(DOT, DOT, DOT))
                .expectNext(asList(LINE, LINE, LINE))
                .expectNext(singletonList(WORD_SEPARATOR))
                .expectNext(asList(DOT, DOT, DOT))
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }

    @Test
    public void fluxCompleteIfEOFIsReached() {
        final Flux<SignalValue> signal = Flux.just(
                DOT, SPACE, DOT, SPACE, DOT, LETTER_SEPARATOR,
                LINE, SPACE, LINE, SPACE, LINE, WORD_SEPARATOR,
                DOT, SPACE, DOT, SPACE, DOT, LETTER_SEPARATOR,
                DOT, SPACE, LINE, SPACE, DOT, SPACE, LINE, SPACE, DOT, SPACE, LINE, LETTER_SEPARATOR,
                DOT, SPACE, DOT, SPACE, DOT, LETTER_SEPARATOR);

        StepVerifier.create(segmentation.chunk(signal))
                .expectNext(asList(DOT, DOT, DOT))
                .expectNext(asList(LINE, LINE, LINE))
                .expectNext(singletonList(WORD_SEPARATOR))
                .expectNext(asList(DOT, DOT, DOT))
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }
}
