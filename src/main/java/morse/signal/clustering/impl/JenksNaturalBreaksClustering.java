package morse.signal.clustering.impl;

import morse.signal.clustering.DiscreteClusters;
import morse.signal.clustering.OneDimensionDiscreteClustering;
import morse.utils.statistics.Mean;
import morse.utils.statistics.Minimizer;
import morse.utils.statistics.Range;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class JenksNaturalBreaksClustering<CLASS>
        implements OneDimensionDiscreteClustering<CLASS, Integer> {
    @Override
    public DiscreteClusters<CLASS, Integer> getClusters(
            CLASS firstClass,
            CLASS secondClass,
            Collection<Integer> samples) {

        List<Integer> sortedSamples = samples.stream()
                .sorted()
                .collect(Collectors.toCollection(ArrayList::new));

        Minimizer sdcmMinimizer = new Minimizer(
                new Range<>(1, sortedSamples.size() - 1),
                pivot -> sdcmAll(sortedSamples, pivot));

        int pivot = sdcmMinimizer.relativeMin(samples.size()/2);

        return new OneDimensionDiscreteClusters<>(firstClass, secondClass, sortedSamples, pivot);
    }

    /**
     * Calculate the sum of squared deviations of each cluster.
     *
     * @param samples
     * @param pivot
     * @return Sum of all squared deviations.
     */
    private double sdcmAll(List<Integer> samples, int pivot) {
        return sdcm(samples.subList(0, pivot)) + sdcm(samples.subList(pivot, samples.size()));
    }

    /**
     * Calculate the sum of squared deviations.
     *
     * @param samples
     * @return Sum of squared deviations.
     */
    private double sdcm(List<Integer> samples) {
        final Mean mean = new Mean(samples);
        return samples.stream().mapToDouble(mean::squaredDeviation).sum();
    }
}
