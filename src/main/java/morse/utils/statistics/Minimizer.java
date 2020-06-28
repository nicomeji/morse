package morse.utils.statistics;

import lombok.Value;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.function.Function;
import java.util.function.IntUnaryOperator;

@Value
public class Minimizer {
    private final Range<Integer> domain;
    private final Function<Integer, Double> functionToMinimize;

    public int relativeMin(final int pivot) {
        if (!domain.contains(pivot)) {
            throw new IllegalArgumentException();
        }
        Tuple2<Integer, Double> initial = Tuples.of(pivot, functionToMinimize.apply(pivot));
        Tuple2<Integer, Double> searchUnder = searchMin(initial, i -> i - 1);
        Tuple2<Integer, Double> searchAbove = searchMin(initial, i -> i + 1);
        if (initial.getT2() <= searchUnder.getT2() && initial.getT2() <= searchAbove.getT2()) {
            return initial.getT1();
        } else if (searchUnder.getT2() <= searchAbove.getT2()) {
            return searchUnder.getT1();
        } else {
            return searchAbove.getT1();
        }
    }

    private Tuple2<Integer, Double> searchMin(
            Tuple2<Integer, Double> initial,
            IntUnaryOperator updateValue) {
        int previousValue = initial.getT1();
        double previousResult = initial.getT2();
        int actualValue = updateValue.applyAsInt(previousValue);
        while (domain.contains(actualValue)) {
            double actualResult = functionToMinimize.apply(actualValue);
            if (previousResult >= actualResult) {
                previousValue = actualValue;
                previousResult = actualResult;
                actualValue = updateValue.applyAsInt(previousValue);
            } else {
                break;
            }
        }
        return Tuples.of(previousValue, previousResult);
    }
}
