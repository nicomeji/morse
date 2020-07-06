package morse.models;

import lombok.Value;

@Value
public class SignalMeaning {
    private final String morse;
    private final Character character;
}
