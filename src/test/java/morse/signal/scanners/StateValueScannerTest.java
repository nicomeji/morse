package morse.signal.scanners;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.clustering.JenksNaturalBreaksClustering;
import morse.signal.converters.StateConverter;
import morse.signal.converters.StateConverterFactory;
import morse.utils.scanners.FluxScanner;
import morse.utils.statistics.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StateValueScannerTest {
    private static final Range<Integer> SHORT_SIGNAL = new Range<>(30, 50);
    private static final Range<Integer> LONG_SIGNAL = new Range<>(100, 150);
    private static final SignalState SIGNAL_STATE = new SignalState(SignalState.State.UP, 40);

    @Mock
    private JenksNaturalBreaksClustering clustering;

    @Mock
    private StateConverterFactory stateConverterFactory;

    @InjectMocks
    private StateValueScannerFactory scannerFactory;

    @Test
    public void testSignalScanning() {
        when(clustering.getClusters(any())).thenReturn(asList(SHORT_SIGNAL, LONG_SIGNAL));

        StateConverter stateConverter = mock(StateConverter.class);
        when(stateConverterFactory.create(SHORT_SIGNAL, LONG_SIGNAL)).thenReturn(stateConverter);
        when(stateConverter.toSignalValue(any())).thenReturn(SignalValue.DOT);

        FluxScanner.Scanner<SignalState, SignalValue> scanner = scannerFactory.get();

        int totalBuffer = UndeterminedStateValueScanner.MAX_SAMPLES_QTY + UnstableStateValueScanner.MAX_SAMPLES_QTY;
        final int samplesQuantity = totalBuffer * 10;

        List<SignalValue> values = new LinkedList<>();
        Stream.generate(() -> SIGNAL_STATE)
                .limit(samplesQuantity)
                .forEach(s -> scanner.map(s, values::add));
        scanner.complete(values::add);

        assertEquals(samplesQuantity, values.size());
        verify(clustering).getClusters(any());
        verify(stateConverterFactory).create(SHORT_SIGNAL, LONG_SIGNAL);
        verify(stateConverter, times(samplesQuantity)).toSignalValue(SIGNAL_STATE);
    }
}
