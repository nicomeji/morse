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

import static java.util.Arrays.asList;
import static morse.models.SignalValue.DOT;
import static morse.models.SignalValue.LINE;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SignalTranslatorTest {
    private static final SignalMeaning MORSE_A = new SignalMeaning(".-", 'a');
    private static final SignalMeaning MORSE_B = new SignalMeaning("-...", 'b');
    private static final SignalMeaning MORSE_C = new SignalMeaning("-.-.", 'c');

    @Mock
    private MorseTranslator translator;

    @Mock
    private SignalSegmentation segmentation;

    @InjectMocks
    private SignalTranslator signalTranslator;

    @Test
    public void validateSignalOrdering() {
        final Duration delay = Duration.ofMillis(100);
        final Flux<SignalValue> signal = Flux.empty();

        when(translator.translate(MORSE_A.getMorse())).thenReturn(Mono.just(MORSE_A));
        when(translator.translate(MORSE_B.getMorse())).thenReturn(Mono.just(MORSE_B).delayElement(delay));
        when(translator.translate(MORSE_C.getMorse())).thenReturn(Mono.just(MORSE_C));

        when(segmentation.chunk(signal))
                .thenReturn(Flux.just(
                        asList(DOT, LINE),
                        asList(LINE, DOT, DOT, DOT),
                        asList(LINE, DOT, LINE, DOT)));

        StepVerifier.create(signalTranslator.translate(signal))
                .expectNext(MORSE_A)
                .expectNext(MORSE_B)
                .expectNext(MORSE_C)
                .expectComplete()
                .verify(Duration.ofSeconds(3));

        verify(segmentation).chunk(signal);
        verify(translator).translate(MORSE_A.getMorse());
        verify(translator).translate(MORSE_B.getMorse());
        verify(translator).translate(MORSE_C.getMorse());
    }
}
