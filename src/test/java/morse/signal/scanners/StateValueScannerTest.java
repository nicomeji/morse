package morse.signal.scanners;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.clustering.JenksNaturalBreaksClustering;
import morse.utils.mappers.FluxScanner;
import morse.utils.statistics.Range;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static morse.models.SignalValue.UNDEFINED;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StateValueScannerTest {
    private static final Range<Integer> SHORT_SIGNAL = new Range<>(30, 50);
    private static final Range<Integer> LONG_SIGNAL = new Range<>(100, 150);

    @Mock
    private JenksNaturalBreaksClustering clustering;

    @InjectMocks
    private StateValueScannerFactory scannerFactory;

    @Test
    public void testSignalScanning() {
        when(clustering.getClusters(any())).thenReturn(asList(SHORT_SIGNAL, LONG_SIGNAL));

        FluxScanner.Scanner<SignalState, SignalValue> scanner = scannerFactory.get();

        int totalBuffer = UndeterminedStateValueScanner.MAX_SAMPLES_QTY + UnstableStateValueScanner.MAX_SAMPLES_QTY;

        List<SignalValue> values = new LinkedList<>();
        generateSignal(totalBuffer * 10).forEach(s -> scanner.map(s, values::add));
        scanner.complete(values::add);

        assertEquals(totalBuffer * 10, values.size());
        verify(clustering).getClusters(any());
    }

    private List<SignalState> generateSignal(int length) {
        return IntStream.range(0, length)
                .mapToObj(i -> {
                    SignalState.State state = i % 2 == 0 ? SignalState.State.UP : SignalState.State.DOWN;
                    int duration = i % 4 < 2 ? 40 : 120;
                    return new SignalState(state, duration);
                }).collect(Collectors.toList());

    }
}
