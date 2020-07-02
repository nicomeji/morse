package morse.signal.scanners;

import lombok.AllArgsConstructor;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.clustering.JenksNaturalBreaksClustering;
import morse.utils.mappers.FluxScanner.Scanner;
import morse.utils.statistics.Range;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Component
@AllArgsConstructor
public class StateValueScannerFactory implements Supplier<Scanner<SignalState, SignalValue>> {
    private final JenksNaturalBreaksClustering clustering;

    @Override
    public Scanner<SignalState, SignalValue> get() {
        StateValueScanner stateValueScanner = new StateValueScanner();
        stateValueScanner.setDelegate(new UndeterminedStateValueScanner(stateValueScanner, this));
        return stateValueScanner;
    }

    UnstableStateValueScanner unstable(StateValueScanner context, List<SignalState> buffer) {
        return new UnstableStateValueScanner(context, buffer, clustering, this);
    }

    StableStateValueScanner stable(
            StateValueScanner context,
            Map<SignalState.State, Map<Range<Integer>, SignalValue>> ranges) {
        return new StableStateValueScanner(context, ranges, this);
    }
}
