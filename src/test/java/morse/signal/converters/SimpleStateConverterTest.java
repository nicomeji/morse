package morse.signal.converters;

import morse.models.SignalState;
import morse.models.SignalState.State;
import morse.models.SignalValue;
import morse.utils.statistics.Range;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class SimpleStateConverterTest {
    @Test
    public void testConversion() {
        final StateConverter nextConverter = mock(StateConverter.class);

        final StateConverter converter = new SimpleStateConverter(
                nextConverter,
                new Range<>(10, 15),
                State.UP,
                SignalValue.DOT);

        assertEquals(SignalValue.DOT, converter.toSignalValue(new SignalState(State.UP, 12)));
        verify(nextConverter, never()).toSignalValue(any());
    }

    @Test
    public void forwardCallToNextConverterIfStateMissMatch() {
        final SignalState signalState = new SignalState(State.DOWN, 12);
        final StateConverter nextConverter = mock(StateConverter.class);
        when(nextConverter.toSignalValue(signalState)).thenReturn(SignalValue.SPACE);

        final StateConverter converter = new SimpleStateConverter(
                nextConverter,
                new Range<>(10, 15),
                State.UP,
                SignalValue.DOT);

        assertEquals(SignalValue.SPACE, converter.toSignalValue(signalState));
        verify(nextConverter).toSignalValue(signalState);
    }

    @Test
    public void forwardCallToNextConverterIfRangeMissMatch() {
        final SignalState signalState = new SignalState(State.UP, 30);
        final StateConverter nextConverter = mock(StateConverter.class);
        when(nextConverter.toSignalValue(signalState)).thenReturn(SignalValue.LINE);

        final StateConverter converter = new SimpleStateConverter(
                nextConverter,
                new Range<>(10, 15),
                State.UP,
                SignalValue.DOT);

        assertEquals(SignalValue.LINE, converter.toSignalValue(signalState));
        verify(nextConverter).toSignalValue(signalState);
    }

    @Test
    public void undefinedIfRangeMissMatchAndMissingNextConverter() {
        final StateConverter converter = new SimpleStateConverter(
                null,
                new Range<>(10, 15),
                State.UP,
                SignalValue.DOT);

        assertEquals(SignalValue.UNDEFINED, converter.toSignalValue(new SignalState(State.UP, 30)));
    }

    @Test
    public void undefinedIfStateMissMatchAndMissingNextConverter() {
        final StateConverter converter = new SimpleStateConverter(
                null,
                new Range<>(10, 15),
                State.UP,
                SignalValue.DOT);

        assertEquals(SignalValue.UNDEFINED, converter.toSignalValue(new SignalState(State.DOWN, 12)));
    }
}
