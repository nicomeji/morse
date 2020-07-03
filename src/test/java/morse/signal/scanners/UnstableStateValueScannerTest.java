package morse.signal.scanners;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.clustering.JenksNaturalBreaksClustering;
import morse.utils.statistics.Range;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static java.util.Arrays.asList;
import static morse.models.SignalValue.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class UnstableStateValueScannerTest {
    private static final Map<SignalState.State, Map<Range<Integer>, SignalValue>> RANGES;
    private static final Range<Integer> SHORT_SIGNAL = new Range<>(3, 5);
    private static final Range<Integer> LONG_SIGNAL = new Range<>(9, 15);

    static {
        Map<Range<Integer>, SignalValue> upValueMap = Map.of(
                SHORT_SIGNAL, DOT,
                LONG_SIGNAL, LINE);

        Map<Range<Integer>, SignalValue> downValueMap = Map.of(
                SHORT_SIGNAL, SPACE,
                LONG_SIGNAL, BREAK);

        RANGES = Map.of(
                SignalState.State.UP, upValueMap,
                SignalState.State.DOWN, downValueMap);
    }

    @Test
    public void scanHasNoEffect() {
        LinkedList<SignalState> buffer = new LinkedList<>();
        StateValueScanner context = mock(StateValueScanner.class);
        StateValueScannerFactory scannerFactory = mock(StateValueScannerFactory.class);
        JenksNaturalBreaksClustering clustering = mock(JenksNaturalBreaksClustering.class);
        UnstableStateValueScanner scanner = new UnstableStateValueScanner(context, buffer, clustering, scannerFactory);

        List<SignalValue> values = new LinkedList<>();
        scanner.map(new SignalState(SignalState.State.UP, 6), values::add);

        assertTrue(values.isEmpty());
        verify(context, never()).setDelegate(any());
    }

    @Test
    public void delegateProcessingOnceBufferIsCompleted() {
        List<SignalState> signal = IntStream.rangeClosed(0, UnstableStateValueScanner.MAX_SAMPLES_QTY)
                .mapToObj(i -> i % 2 == 0 ? SignalState.State.UP : SignalState.State.DOWN)
                .map(s -> new SignalState(s, 6))
                .collect(Collectors.toList());
        LinkedList<SignalState> buffer = new LinkedList<>();
        StateValueScanner context = mock(StateValueScanner.class);
        StateValueScannerFactory scannerFactory = mock(StateValueScannerFactory.class);
        JenksNaturalBreaksClustering clustering = mock(JenksNaturalBreaksClustering.class);
        UnstableStateValueScanner scanner = new UnstableStateValueScanner(context, buffer, clustering, scannerFactory);

        when(clustering.getClusters(any())).thenReturn(asList(SHORT_SIGNAL, LONG_SIGNAL));
        StableStateValueScanner nextScanner = mock(StableStateValueScanner.class);
        when(scannerFactory.stable(context, RANGES)).thenReturn(nextScanner);
        doNothing().when(nextScanner).map(any(), any());

        List<SignalValue> values = new LinkedList<>();
        signal.forEach(s -> scanner.map(s, values::add));

        assertTrue(values.isEmpty());
        verify(scannerFactory).stable(context, RANGES);
        verify(nextScanner, times(UnstableStateValueScanner.MAX_SAMPLES_QTY + 1)).map(any(), any());
        verify(context).setDelegate(any());
    }

    @Test
    public void delegateProcessingOnComplete() {
        LinkedList<SignalState> buffer = new LinkedList<>();
        StateValueScanner context = mock(StateValueScanner.class);
        StateValueScannerFactory scannerFactory = mock(StateValueScannerFactory.class);
        JenksNaturalBreaksClustering clustering = mock(JenksNaturalBreaksClustering.class);
        UnstableStateValueScanner scanner = new UnstableStateValueScanner(context, buffer, clustering, scannerFactory);

        when(clustering.getClusters(any())).thenReturn(asList(SHORT_SIGNAL, LONG_SIGNAL));
        StableStateValueScanner nextScanner = mock(StableStateValueScanner.class);
        when(scannerFactory.stable(context, RANGES)).thenReturn(nextScanner);
        doNothing().when(nextScanner).map(any(), any());
        doNothing().when(nextScanner).complete(any());

        List<SignalValue> values = new LinkedList<>();
        scanner.map(new SignalState(SignalState.State.UP, 6), values::add);
        scanner.map(new SignalState(SignalState.State.DOWN, 6), values::add);
        assertTrue(values.isEmpty());

        scanner.complete(values::add);
        assertTrue(values.isEmpty());

        verify(scannerFactory).stable(context, RANGES);
        verify(nextScanner, times(2)).map(any(), any());
        verify(nextScanner).complete(any());
        verify(context, never()).setDelegate(any());
    }
}
