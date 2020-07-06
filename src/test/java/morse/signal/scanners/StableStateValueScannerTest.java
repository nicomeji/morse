package morse.signal.scanners;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.converters.StateConverter;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.util.LinkedList;
import java.util.List;

import static java.util.Collections.singletonList;
import static morse.models.SignalValue.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class StableStateValueScannerTest {
    @Mock
    private StateValueScanner context;

    @Mock
    private StateValueScannerFactory scannerFactory;

    @Mock
    private StateConverter converter;

    @InjectMocks
    private StableStateValueScanner scanner;

    @Test
    public void signalScanCorrectly() {
        final SignalState signalState = new SignalState(SignalState.State.UP, 4);
        when(converter.toSignalValue(signalState)).thenReturn(DOT);

        List<SignalValue> values = new LinkedList<>();
        scanner.map(signalState, values::add);

        assertEquals(singletonList(DOT), values);
        verify(converter).toSignalValue(signalState);
        verify(context, never()).setDelegate(any());
    }

    @Test
    public void switchDelegateIfStateIsUndefined() {
        final UnstableStateValueScanner nextScanner = mock(UnstableStateValueScanner.class);
        final SignalState unmatchedState = new SignalState(SignalState.State.UP, 6);
        when(converter.toSignalValue(unmatchedState)).thenReturn(UNDEFINED);
        when(scannerFactory.unstable(context, singletonList(unmatchedState))).thenReturn(nextScanner);

        List<SignalValue> values = new LinkedList<>();
        scanner.map(unmatchedState, values::add);

        assertTrue(values.isEmpty());
        verify(scannerFactory).unstable(context, singletonList(unmatchedState));
        verify(converter).toSignalValue(unmatchedState);
        verify(context).setDelegate(nextScanner);
    }

    @Test
    public void completeHasNoEffect() {
        List<SignalValue> values = new LinkedList<>();
        scanner.complete(values::add);

        assertTrue(values.isEmpty());
        verify(context, never()).setDelegate(any());
    }
}
