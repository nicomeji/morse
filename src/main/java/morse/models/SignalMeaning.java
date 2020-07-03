package morse.models;

import lombok.NonNull;
import lombok.Value;

@Value
public class SignalMeaning {
    @NonNull
    private final String morse;
    private final Character character;
}
