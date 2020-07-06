package morse.translator;

import morse.models.SignalMeaning;
import morse.models.SignalValue;
import morse.remote.MorseTranslator;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;

import static morse.models.SignalValue.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SignalTranslatorTest {
    private static final SignalMeaning MORSE_A = new SignalMeaning(".-", 'a');
    private static final SignalMeaning MORSE_B = new SignalMeaning("-...", 'b');
    private static final SignalMeaning MORSE_C = new SignalMeaning("-.-.", 'c');
    private static final SignalMeaning SEPARATION = new SignalMeaning(null, ' ');

    @Mock
    private MorseTranslator translator;

    @InjectMocks
    private SignalTranslator signalTranslator;

    @Test
    public void validateSignalOrdering() {
        final Duration delay = Duration.ofMillis(100);
        when(translator.translate(MORSE_A.getMorse())).thenReturn(Mono.just(MORSE_A));
        when(translator.translate(MORSE_B.getMorse())).thenReturn(Mono.just(MORSE_B).delayElement(delay));
        when(translator.translate(MORSE_C.getMorse())).thenReturn(Mono.just(MORSE_C));
        Flux<SignalValue> signal = Flux.just(
                /* 'a' */ DOT, SPACE, LINE, BREAK,
                /* 'b' */ LINE, SPACE, DOT, SPACE, DOT, SPACE, DOT, BREAK,
                /* 'c' */ LINE, SPACE, DOT, SPACE, LINE, DOT, BREAK);

        StepVerifier.create(signalTranslator.translate(signal))
                .expectNext(MORSE_A)
                .expectNext(MORSE_B)
                .expectNext(MORSE_C)
                .expectComplete()
                .verify(Duration.ofSeconds(3));

        verify(translator).translate(MORSE_A.getMorse());
        verify(translator).translate(MORSE_B.getMorse());
        verify(translator).translate(MORSE_C.getMorse());
    }

    @Test
    public void validateSignalProcessing() {
        final Duration delay = Duration.ofMillis(100);
        when(translator.translate(MORSE_A.getMorse())).thenReturn(Mono.just(MORSE_A).delayElement(delay));
        when(translator.translate(MORSE_C.getMorse())).thenReturn(Mono.just(MORSE_C));
        Flux<SignalValue> signal = Flux.just(
                /* 'a' and ' ' */ DOT, SPACE, LINE, LONG_SPACE,
                /* 'c' */ LINE, SPACE, DOT, SPACE, LINE, DOT, BREAK);

        StepVerifier.create(signalTranslator.translate(signal))
                .expectNext(MORSE_A)
                .expectNext(SEPARATION)
                .expectNext(MORSE_C)
                .expectComplete()
                .verify(Duration.ofSeconds(3));

        verify(translator).translate(MORSE_A.getMorse());
        verify(translator).translate(MORSE_C.getMorse());
    }

    @Test
    public void validateSignalStopProcessing() {
        when(translator.translate(MORSE_A.getMorse())).thenReturn(Mono.just(MORSE_A));
        when(translator.translate(MORSE_B.getMorse())).thenReturn(Mono.just(MORSE_B));
        when(translator.translate(MORSE_C.getMorse())).thenReturn(Mono.just(MORSE_C));
        Flux<SignalValue> signal = Flux.just(
                /* 'a' */ DOT, SPACE, LINE, BREAK,
                /* 'b' */ LINE, SPACE, DOT, SPACE, DOT, SPACE, DOT, BREAK,
                /* 'b' */ LINE, SPACE, DOT, SPACE, LINE, DOT, BREAK,
                /* EOF */ STOP,
                /* 'a' */ DOT, SPACE, LINE, BREAK);

        StepVerifier.create(signalTranslator.translate(signal))
                .expectNext(MORSE_A)
                .expectNext(MORSE_B)
                .expectNext(MORSE_C)
                .expectComplete()
                .verify(Duration.ofMillis(10));

        verify(translator).translate(MORSE_A.getMorse());
        verify(translator).translate(MORSE_B.getMorse());
        verify(translator).translate(MORSE_C.getMorse());
    }

    @Test
    public void validateSignalEOFProcessing() {
        when(translator.translate(MORSE_A.getMorse())).thenReturn(Mono.just(MORSE_A));
        when(translator.translate(MORSE_B.getMorse())).thenReturn(Mono.just(MORSE_B));
        when(translator.translate(MORSE_C.getMorse())).thenReturn(Mono.just(MORSE_C));
        Flux<SignalValue> signal = Flux.just(
                /* 'a' */ DOT, SPACE, LINE, BREAK,
                /* 'b' */ LINE, SPACE, DOT, SPACE, DOT, SPACE, DOT, BREAK,
                /* 'c' */ LINE, SPACE, DOT, SPACE, LINE, SPACE, DOT, BREAK,
                /* EOF: */ DOT, SPACE, LINE, SPACE, DOT, SPACE, LINE, SPACE, DOT, SPACE, LINE, SPACE, BREAK,
                /* 'a' */ DOT, SPACE, LINE, BREAK);

        StepVerifier.create(signalTranslator.translate(signal))
                .expectNext(MORSE_A)
                .expectNext(MORSE_B)
                .expectNext(MORSE_C)
                .expectComplete()
                .verify(Duration.ofMillis(10));

        verify(translator).translate(MORSE_A.getMorse());
        verify(translator).translate(MORSE_B.getMorse());
        verify(translator).translate(MORSE_C.getMorse());
    }
}
