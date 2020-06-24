package morse.models;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

@Value
public class SignalMeaning {
    @NonNull
    private final List<SignalValue> morse;
    private final java.lang.Character character;
}
