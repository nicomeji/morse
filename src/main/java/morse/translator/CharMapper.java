package morse.translator;

import morse.models.SignalValue;
import org.springframework.stereotype.Component;

import java.util.function.Function;

@Component
public class CharMapper implements Function<SignalValue, Character> {
    @Override
    public Character apply(SignalValue value) {
        switch (value) {
        case DOT: return '.';
        case LINE: return '-';
        default: throw new IllegalArgumentException();
        }
    }
}
