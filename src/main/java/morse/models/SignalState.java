package morse.models;

import lombok.Data;

@Data
public class SignalState {
    public enum State {
        UP, DOWN
    }

    private final State state;
    private final int duration;
}
