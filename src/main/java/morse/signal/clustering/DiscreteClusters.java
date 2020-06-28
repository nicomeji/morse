package morse.signal.clustering;

import java.util.Optional;

public interface DiscreteClusters<CLASS, SAMPLE> {
    Optional<CLASS> clusterOf(SAMPLE sample);
}
