package morse.signal.clustering;

import java.util.Collection;

@FunctionalInterface
public interface OneDimensionDiscreteClustering<CLASS, SAMPLE extends Comparable<SAMPLE>> {
    DiscreteClusters<CLASS, SAMPLE> getClusters(CLASS firstClass, CLASS secondClass, Collection<SAMPLE> samples);
}
