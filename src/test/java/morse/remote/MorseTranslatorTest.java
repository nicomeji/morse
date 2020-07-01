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

import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class MorseTranslatorTest {
    private static final String MORSE_SIGNAL_A = ".-";
    private static final SignalMeaning SIGNAL_MEANING_A = new SignalMeaning(MORSE_SIGNAL_A, 'a');

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
}
