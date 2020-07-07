package morse.signal.converters;

import morse.models.SignalState;
import morse.models.SignalValue;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class StopConverterTest {
    @Test
    public void testConversion() {
        final StateConverter nextConverter = mock(StateConverter.class);
        final StateConverter converter = new StopConverter(nextConverter, 100);
        final SignalState signalState = new SignalState(SignalState.State.DOWN, 120);

        assertEquals(SignalValue.SIGNAL_STOP, converter.toSignalValue(signalState));
        verify(nextConverter, never()).toSignalValue(any());
    }

    @Test
    public void forwardCallToNextConverterIfStateMissMatch() {
        final StateConverter nextConverter = mock(StateConverter.class);
        final StateConverter converter = new StopConverter(nextConverter, 100);
        final SignalState signalState = new SignalState(SignalState.State.UP, 120);
        when(nextConverter.toSignalValue(signalState)).thenReturn(SignalValue.SPACE);

        assertEquals(SignalValue.SPACE, converter.toSignalValue(signalState));
        verify(nextConverter).toSignalValue(signalState);
    }

    @Test
    public void forwardCallToNextConverterIfRangeMissMatch() {
        final StateConverter nextConverter = mock(StateConverter.class);
        final StateConverter converter = new StopConverter(nextConverter, 100);
        final SignalState signalState = new SignalState(SignalState.State.DOWN, 10);
        when(nextConverter.toSignalValue(signalState)).thenReturn(SignalValue.SPACE);

        assertEquals(SignalValue.SPACE, converter.toSignalValue(signalState));
        verify(nextConverter).toSignalValue(signalState);
    }

    @Test
    public void undefinedIfRangeMissMatchAndMissingNextConverter() {
        final StateConverter converter = new StopConverter(null, 100);
        final SignalState signalState = new SignalState(SignalState.State.DOWN, 10);

        assertEquals(SignalValue.UNDEFINED, converter.toSignalValue(signalState));
    }

    @Test
    public void undefinedIfStateMissMatchAndMissingNextConverter() {
        final StateConverter converter = new StopConverter(null, 100);
        final SignalState signalState = new SignalState(SignalState.State.DOWN, 10);

        assertEquals(SignalValue.UNDEFINED, converter.toSignalValue(signalState));
    }
}
