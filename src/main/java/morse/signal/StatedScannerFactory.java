package morse.signal;

import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.scanners.UndeterminedStateValueScanner;
import morse.utils.mappers.FluxScanner.Scanner;
import org.springframework.stereotype.Component;

@Component
public class StatedScannerFactory {
    Scanner<SignalState, SignalValue> create(StateValueMapper stateContext) {
        return new UndeterminedStateValueScanner(stateContext);
    }
}
