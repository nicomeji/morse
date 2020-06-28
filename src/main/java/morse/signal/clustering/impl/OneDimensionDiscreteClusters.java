package morse.signal.clustering.impl;

import lombok.Value;
import morse.signal.clustering.DiscreteClusters;
import morse.utils.statistics.Range;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

@Value
class OneDimensionDiscreteClusters<CLASS> implements DiscreteClusters<CLASS, Integer> {
    private Map<CLASS, Predicate<Integer>> classifier = new HashMap<>();

    public OneDimensionDiscreteClusters(
            CLASS firstClass,
            CLASS secondClass,
            List<Integer> samples,
            final int pivot) {
        classifier.put(firstClass, Range.containing(samples.subList(0, pivot))::contains);
        classifier.put(secondClass, Range.containing(samples.subList(pivot, samples.size()))::contains);
    }

    @Override
    public Optional<CLASS> clusterOf(Integer sample) {
        return classifier.entrySet().stream()
                .filter(entry -> entry.getValue().test(sample))
                .findFirst()
                .map(Map.Entry::getKey);
    }
}
