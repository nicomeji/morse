package morse.signal.scanners;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.utils.mappers.FluxScanner.Scanner;
import morse.utils.statistics.Range;
import org.junit.Test;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;
import static morse.models.SignalValue.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StableStateValueScannerTest {
    private static final Map<SignalState.State, Map<Range<Integer>, SignalValue>> RANGES;

    static {
        Map<Range<Integer>, SignalValue> upValueMap = Map.of(
                new Range<>(3, 5), DOT,
                new Range<>(9, 15), LINE);

        Map<Range<Integer>, SignalValue> downValueMap = Map.of(
                new Range<>(3, 5), BREAK,
                new Range<>(9, 15), SPACE);

        RANGES = Map.of(
                SignalState.State.UP, upValueMap,
                SignalState.State.DOWN, downValueMap);
    }

    @Test
    public void signalScanCorrectly() {
        StateValueScanner context = mock(StateValueScanner.class);
        StateValueScannerFactory scannerFactory = mock(StateValueScannerFactory.class);
        Scanner<SignalState, SignalValue> scanner = new StableStateValueScanner(context, RANGES, scannerFactory);

        List<SignalState> signal = asList(
                new SignalState(SignalState.State.UP, 4),
                new SignalState(SignalState.State.DOWN, 3),
                new SignalState(SignalState.State.UP, 5),
                new SignalState(SignalState.State.DOWN, 4),
                new SignalState(SignalState.State.UP, 10),
                new SignalState(SignalState.State.DOWN, 11),
                new SignalState(SignalState.State.UP, 15));

        List<SignalValue> values = new LinkedList<>();
        signal.forEach(s -> scanner.map(s, values::add));

        assertEquals(asList(DOT, BREAK, DOT, BREAK, LINE, SPACE, LINE), values);
        verify(context, never()).setDelegate(any());
    }

    @Test
    public void switchDelegateIfStateIsUndefined() {
        StateValueScanner context = mock(StateValueScanner.class);
        StateValueScannerFactory scannerFactory = mock(StateValueScannerFactory.class);
        Scanner<SignalState, SignalValue> scanner = new StableStateValueScanner(context, RANGES, scannerFactory);

        UnstableStateValueScanner nextScanner = mock(UnstableStateValueScanner.class);
        SignalState unmatchedState = new SignalState(SignalState.State.UP, 6);
        when(scannerFactory.unstable(context, singletonList(unmatchedState))).thenReturn(nextScanner);

        List<SignalValue> values = new LinkedList<>();
        scanner.map(unmatchedState, values::add);

        assertTrue(values.isEmpty());
        verify(scannerFactory).unstable(context, singletonList(unmatchedState));
        verify(context).setDelegate(nextScanner);
    }

    @Test
    public void completeHasNoEffect() {
        StateValueScanner context = mock(StateValueScanner.class);
        StateValueScannerFactory scannerFactory = mock(StateValueScannerFactory.class);
        Scanner<SignalState, SignalValue> scanner = new StableStateValueScanner(context, RANGES, scannerFactory);

        List<SignalValue> values = new LinkedList<>();
        scanner.complete(values::add);

        assertTrue(values.isEmpty());
        verify(context, never()).setDelegate(any());
    }
}
