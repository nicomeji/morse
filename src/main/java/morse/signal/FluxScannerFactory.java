package morse.signal;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.scanners.StateValueScannerFactory;
import morse.utils.scanners.FluxScanner;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class FluxScannerFactory {
    private final StateValueScannerFactory scannerFactory;

    public FluxScanner<SignalState, SignalValue> create() {
        return new FluxScanner<>(scannerFactory.create());
    }
}
