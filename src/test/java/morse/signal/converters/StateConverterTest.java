package morse.signal.converters;

import morse.models.SignalState;
import morse.models.SignalState.State;
import morse.utils.statistics.Range;
import org.junit.Test;

import static morse.models.SignalValue.*;
import static org.junit.Assert.assertEquals;

public class StateConverterTest {
    private static final StateConverter CONVERTER = new StateConverterFactory()
            .create(new Range<>(15, 20), new Range<>(30, 35));

    @Test
    public void spaceConversion() {
        assertEquals(UNDEFINED, CONVERTER.toSignalValue(new SignalState(State.DOWN, 14)));
        assertEquals(SPACE, CONVERTER.toSignalValue(new SignalState(State.DOWN, 15)));
        assertEquals(SPACE, CONVERTER.toSignalValue(new SignalState(State.DOWN, 17)));
        assertEquals(SPACE, CONVERTER.toSignalValue(new SignalState(State.DOWN, 20)));
        assertEquals(UNDEFINED, CONVERTER.toSignalValue(new SignalState(State.DOWN, 21)));
    }

    @Test
    public void dotConversion() {
        assertEquals(UNDEFINED, CONVERTER.toSignalValue(new SignalState(State.UP, 14)));
        assertEquals(DOT, CONVERTER.toSignalValue(new SignalState(State.UP, 15)));
        assertEquals(DOT, CONVERTER.toSignalValue(new SignalState(State.UP, 17)));
        assertEquals(DOT, CONVERTER.toSignalValue(new SignalState(State.UP, 20)));
        assertEquals(UNDEFINED, CONVERTER.toSignalValue(new SignalState(State.UP, 21)));
    }

    @Test
    public void lineConversion() {
        assertEquals(UNDEFINED, CONVERTER.toSignalValue(new SignalState(State.UP, 29)));
        assertEquals(LINE, CONVERTER.toSignalValue(new SignalState(State.UP, 30)));
        assertEquals(LINE, CONVERTER.toSignalValue(new SignalState(State.UP, 32)));
        assertEquals(LINE, CONVERTER.toSignalValue(new SignalState(State.UP, 35)));
        assertEquals(UNDEFINED, CONVERTER.toSignalValue(new SignalState(State.UP, 36)));
    }

    @Test
    public void letterSeparatorConversion() {
        assertEquals(UNDEFINED, CONVERTER.toSignalValue(new SignalState(State.DOWN, 29)));
        assertEquals(LETTER_SEPARATOR, CONVERTER.toSignalValue(new SignalState(State.DOWN, 30)));
        assertEquals(LETTER_SEPARATOR, CONVERTER.toSignalValue(new SignalState(State.DOWN, 32)));
        assertEquals(LETTER_SEPARATOR, CONVERTER.toSignalValue(new SignalState(State.DOWN, 35)));
        assertEquals(UNDEFINED, CONVERTER.toSignalValue(new SignalState(State.DOWN, 36)));
    }

    @Test
    public void wordSeparatorConversion() {
        assertEquals(UNDEFINED, CONVERTER.toSignalValue(new SignalState(State.DOWN, 80)));
        assertEquals(WORD_SEPARATOR, CONVERTER.toSignalValue(new SignalState(State.DOWN, 105)));
        assertEquals(WORD_SEPARATOR, CONVERTER.toSignalValue(new SignalState(State.DOWN, 140)));
        assertEquals(SIGNAL_STOP, CONVERTER.toSignalValue(new SignalState(State.DOWN, 400)));
    }
}
