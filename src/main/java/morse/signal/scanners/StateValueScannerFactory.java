package morse.signal.scanners;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morse.models.SignalState;
import morse.models.SignalValue;
import morse.signal.clustering.JenksNaturalBreaksClustering;
import morse.signal.converters.StateConverter;
import morse.signal.converters.StateConverterFactory;
import morse.utils.scanners.FluxScanner.Scanner;
import morse.utils.statistics.Range;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@AllArgsConstructor
public class StateValueScannerFactory {
    private final JenksNaturalBreaksClustering clustering;
    private final StateConverterFactory stateConverterFactory;

    public Scanner<SignalState, SignalValue> create() {
        log.info("CREATE UNDETERMINED");
        StateValueScanner stateValueScanner = new StateValueScanner();
        stateValueScanner.setDelegate(new UndeterminedStateValueScanner(stateValueScanner, this));
        return stateValueScanner;
    }

    UnstableStateValueScanner unstable(StateValueScanner context, List<SignalState> buffer) {
        log.info("CREATE UNSTABLE");
        return new UnstableStateValueScanner(context, buffer, clustering, this);
    }

    StableStateValueScanner stable(
            StateValueScanner context,
            Range<Integer> shortSignalRange,
            Range<Integer> longSignalRange) {
        log.info("CREATE STABLE");
        final StateConverter stateConverter = stateConverterFactory.create(shortSignalRange, longSignalRange);
        return new StableStateValueScanner(context, stateConverter, this);
    }
}
