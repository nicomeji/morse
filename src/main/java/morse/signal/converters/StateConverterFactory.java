package morse.signal.converters;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.utils.statistics.Range;
import org.springframework.stereotype.Component;

@Component
public class StateConverterFactory {
    public StateConverter create(Range<Integer> shortSignalRange, Range<Integer> longSignalRange) {
        // The space between words is 7 dot's length.
        final Range<Integer> veryLongSignalRange = new Range<>(
                shortSignalRange.getFrom() * 6,
                shortSignalRange.getTo() * 10);

        final StopConverter stopConverter = new StopConverter(null, veryLongSignalRange.getTo());
        final StateConverter wordSeparatorConverter = new SimpleStateConverter(
                stopConverter,
                veryLongSignalRange,
                SignalState.State.DOWN,
                SignalValue.WORD_SEPARATOR);
        final StateConverter letterSeparatorConverter = new SimpleStateConverter(
                wordSeparatorConverter,
                longSignalRange,
                SignalState.State.DOWN,
                SignalValue.LETTER_SEPARATOR);
        final StateConverter spaceConverter = new SimpleStateConverter(
                letterSeparatorConverter,
                shortSignalRange,
                SignalState.State.DOWN,
                SignalValue.SPACE);
        final StateConverter dotConverter = new SimpleStateConverter(
                spaceConverter,
                shortSignalRange,
                SignalState.State.UP,
                SignalValue.DOT);
        return new SimpleStateConverter(
                dotConverter,
                longSignalRange,
                SignalState.State.UP,
                SignalValue.LINE);
    }
}
