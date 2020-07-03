package morse.translator;

import morse.models.SignalMeaning;
import morse.models.SignalValue;

import static morse.models.SignalValue.*;

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

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SignalTranslatorTest {
    private static final SignalMeaning MORSE_A = new SignalMeaning(".-", 'a');
    private static final SignalMeaning MORSE_B = new SignalMeaning("-...", 'b');
    private static final SignalMeaning MORSE_C = new SignalMeaning("-.-.", 'c');

    @Mock
    private MorseTranslator translator;

    @InjectMocks
    private SignalTranslator signalTranslator;

    @Test
    public void validateSignalProcessing() {
        when(translator.translate(MORSE_A.getMorse())).thenReturn(Mono.just(MORSE_A));
        when(translator.translate(MORSE_B.getMorse())).thenReturn(Mono.just(MORSE_B));
        when(translator.translate(MORSE_C.getMorse())).thenReturn(Mono.just(MORSE_C));
        Flux<SignalValue> signal = Flux.just(
                /* A: */ DOT, SPACE, LINE, BREAK,
                /* B: */ LINE, SPACE, DOT, SPACE, DOT, SPACE, DOT, BREAK,
                /* C: */ LINE, SPACE, DOT, SPACE, LINE, DOT, BREAK);

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
    public void validateSignalStopProcessing() {
        when(translator.translate(MORSE_A.getMorse())).thenReturn(Mono.just(MORSE_A));
        when(translator.translate(MORSE_B.getMorse())).thenReturn(Mono.just(MORSE_B));
        when(translator.translate(MORSE_C.getMorse())).thenReturn(Mono.just(MORSE_C));
        Flux<SignalValue> signal = Flux.just(
                /* A: */ DOT, SPACE, LINE, BREAK,
                /* B: */ LINE, SPACE, DOT, SPACE, DOT, SPACE, DOT, BREAK,
                /* C: */ LINE, SPACE, DOT, SPACE, LINE, DOT, BREAK,
                /* end: */ STOP,
                /* A: */ DOT, SPACE, LINE, BREAK);

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
                /* A: */ DOT, SPACE, LINE, BREAK,
                /* B: */ LINE, SPACE, DOT, SPACE, DOT, SPACE, DOT, BREAK,
                /* C: */ LINE, SPACE, DOT, SPACE, LINE, SPACE, DOT, BREAK,
                /* EOF: */ DOT, SPACE, LINE, SPACE, DOT, SPACE, LINE, SPACE, DOT, SPACE, LINE, SPACE, BREAK,
                /* A: */ DOT, SPACE, LINE, BREAK);

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
