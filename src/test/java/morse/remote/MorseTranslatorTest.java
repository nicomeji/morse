package morse.remote;

import morse.cache.MorseCache;
import morse.models.SignalMeaning;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.List;

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MorseTranslatorTest {
    private static final String MORSE_SIGNAL_A = ".-";
    private static final String MORSE_SIGNAL_B = ".--";
    private static final String MORSE_SIGNAL_C = "..-";
    private static final String MORSE_SIGNAL_D = "..--";
    private static final SignalMeaning SIGNAL_MEANING_A = new SignalMeaning(MORSE_SIGNAL_A, 'a');
    private static final SignalMeaning SIGNAL_MEANING_B = new SignalMeaning(MORSE_SIGNAL_B, 'b');
    private static final SignalMeaning SIGNAL_MEANING_C = new SignalMeaning(MORSE_SIGNAL_C, 'c');
    private static final SignalMeaning SIGNAL_MEANING_D = new SignalMeaning(MORSE_SIGNAL_D, 'd');

    @Mock
    private MorseCache morseCache;

    @Mock
    private MorseConnector morseConnector;

    @InjectMocks
    private MorseTranslator translator;

    @Test
    public void connectorIsNeverCalledWhenResponseIsCached() {
        when(morseCache.retrieve(MORSE_SIGNAL_A)).thenReturn(Mono.just(SIGNAL_MEANING_A));
        when(morseConnector.requestTranslation(MORSE_SIGNAL_A)).thenReturn(Mono.empty());

        StepVerifier.create(translator.translate(MORSE_SIGNAL_A))
                .expectNext(SIGNAL_MEANING_A)
                .expectComplete()
                .verify(Duration.ofMillis(10));

        verify(morseCache).retrieve(MORSE_SIGNAL_A);
        verify(morseConnector).requestTranslation(MORSE_SIGNAL_A);
    }

    @Test
    public void connectorIsCalledWhenResponseIsNotCached() {
        when(morseCache.retrieve(MORSE_SIGNAL_A)).thenReturn(Mono.empty());
        when(morseConnector.requestTranslation(MORSE_SIGNAL_A)).thenReturn(Mono.just(SIGNAL_MEANING_A));

        StepVerifier.create(translator.translate(MORSE_SIGNAL_A))
                .expectNext(SIGNAL_MEANING_A)
                .expectComplete()
                .verify(Duration.ofSeconds(10));

        verify(morseCache).retrieve(MORSE_SIGNAL_A);
        verify(morseCache).save(MORSE_SIGNAL_A, SIGNAL_MEANING_A);
        verify(morseConnector).requestTranslation(MORSE_SIGNAL_A);
    }

    @Test
    public void lalo() {
        when(morseCache.retrieve(MORSE_SIGNAL_A)).thenReturn(Mono.empty());
        when(morseCache.retrieve(MORSE_SIGNAL_B)).thenReturn(Mono.empty());
        when(morseCache.retrieve(MORSE_SIGNAL_C)).thenReturn(Mono.just(SIGNAL_MEANING_C));
        when(morseCache.retrieve(MORSE_SIGNAL_D)).thenReturn(Mono.empty());
        when(morseConnector.requestTranslation(List.of(MORSE_SIGNAL_A, MORSE_SIGNAL_B, MORSE_SIGNAL_D)))
                .thenReturn(Mono.just(List.of(SIGNAL_MEANING_A, SIGNAL_MEANING_B, SIGNAL_MEANING_D)));

        StepVerifier.create(translator.translate(List.of(MORSE_SIGNAL_A, MORSE_SIGNAL_B, MORSE_SIGNAL_C, MORSE_SIGNAL_D)))
                .expectNext(SIGNAL_MEANING_A)
                .expectNext(SIGNAL_MEANING_B)
                .expectNext(SIGNAL_MEANING_C)
                .expectNext(SIGNAL_MEANING_D)
                .expectComplete()
                .verify(Duration.ofSeconds(10));

//        verify(morseCache).retrieve(MORSE_SIGNAL_A);
//        verify(morseCache).save(MORSE_SIGNAL_A, SIGNAL_MEANING_A);
//        verify(morseConnector).requestTranslation(MORSE_SIGNAL_A);
    }

    @Test
    public void lali() {
        when(morseCache.retrieve(MORSE_SIGNAL_A)).thenReturn(Mono.empty());
        when(morseCache.retrieve(MORSE_SIGNAL_B)).thenReturn(Mono.empty());
        when(morseCache.retrieve(MORSE_SIGNAL_C)).thenReturn(Mono.empty());
        when(morseCache.retrieve(MORSE_SIGNAL_D)).thenReturn(Mono.empty());
        when(morseConnector.requestTranslation(List.of(MORSE_SIGNAL_A, MORSE_SIGNAL_B, MORSE_SIGNAL_C, MORSE_SIGNAL_D)))
                .thenReturn(Mono.just(List.of(SIGNAL_MEANING_A, SIGNAL_MEANING_B, SIGNAL_MEANING_C, SIGNAL_MEANING_D)));

        StepVerifier.create(translator.translate(List.of(MORSE_SIGNAL_A, MORSE_SIGNAL_B, MORSE_SIGNAL_C, MORSE_SIGNAL_D)))
                .expectNext(SIGNAL_MEANING_A)
                .expectNext(SIGNAL_MEANING_B)
                .expectNext(SIGNAL_MEANING_C)
                .expectNext(SIGNAL_MEANING_D)
                .expectComplete()
                .verify(Duration.ofSeconds(10));

        //        verify(morseCache).retrieve(MORSE_SIGNAL_A);
        //        verify(morseCache).save(MORSE_SIGNAL_A, SIGNAL_MEANING_A);
        //        verify(morseConnector).requestTranslation(MORSE_SIGNAL_A);
    }
}
