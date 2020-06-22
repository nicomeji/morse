package morse.models;

import lombok.NonNull;
import lombok.Value;

import java.util.List;

import static java.util.Arrays.asList;
import static morse.models.SignalValue.DOT;
import static morse.models.SignalValue.LINE;

@Value
public class SignalMeaning {
    public static final SignalMeaning EOF = new SignalMeaning(asList(DOT, LINE, DOT, LINE, DOT, LINE), null);

    @NonNull
    private final List<SignalValue> morse;
    private final java.lang.Character character;
}
