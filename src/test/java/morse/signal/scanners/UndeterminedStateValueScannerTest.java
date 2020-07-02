package morse.signal.scanners;

import morse.models.SignalState;
import morse.models.SignalValue;
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
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class UndeterminedStateValueScannerTest {
    @Mock
    private StateValueScanner context;

    @Mock
    private StateValueScannerFactory scannerFactory;

    @InjectMocks
    private UndeterminedStateValueScanner scanner;

    @Test
    public void scanHasNoEffect() {
        List<SignalValue> values = new LinkedList<>();
        scanner.map(new SignalState(SignalState.State.UP, 6), values::add);

        assertTrue(values.isEmpty());
        verify(context, never()).setDelegate(any());
    }

    @Test
    public void completeGivesAnUndefinedValue() {
        List<SignalValue> values = new LinkedList<>();
        scanner.map(new SignalState(SignalState.State.UP, 6), values::add);
        scanner.map(new SignalState(SignalState.State.DOWN, 6), values::add);
        assertTrue(values.isEmpty());

        scanner.complete(values::add);
        assertEquals(asList(UNDEFINED, UNDEFINED), values);

        verify(context, never()).setDelegate(any());
    }

    @Test
    public void delegateProcessingOnceBufferIsCompleted() {
        List<SignalState> signal = IntStream.rangeClosed(0, UndeterminedStateValueScanner.MAX_SAMPLES_QTY)
                .mapToObj(i -> i % 2 == 0 ? SignalState.State.UP : SignalState.State.DOWN)
                .map(s -> new SignalState(s, 6))
                .collect(Collectors.toList());
        UnstableStateValueScanner nextScanner = mock(UnstableStateValueScanner.class);
        when(scannerFactory.unstable(context, signal)).thenReturn(nextScanner);

        List<SignalValue> values = new LinkedList<>();
        signal.forEach(s -> scanner.map(s, values::add));

        assertTrue(values.isEmpty());
        verify(scannerFactory).unstable(context, signal);
        verify(context).setDelegate(nextScanner);
    }
}
