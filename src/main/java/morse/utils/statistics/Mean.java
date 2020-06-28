package morse.utils.statistics;

import lombok.EqualsAndHashCode;
import lombok.Value;

import java.util.List;

@Value
@EqualsAndHashCode(callSuper = false)
public class Mean {
    private final int summation;
    private final int quantity;
    private final double value;

    public Mean(List<Integer> samples) {
        quantity = samples.size();
        summation = samples.stream().reduce(0, Integer::sum);
        value = (double) summation / quantity;
    }

    public double squaredDeviation(int sample) {
        return Math.pow((double) sample - value, 2);
    }
}
