package morse.utils.statistics;

import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.Assert.assertEquals;

public class MeanTest {
    @Test
    public void meanOfSingleValueIsSameValue() {
        Mean mean = new Mean(Collections.singletonList(7));

        assertEquals(1, mean.getQuantity());
        assertEquals(7, mean.getSummation());
        assertEquals(7d, mean.getValue(), 0.000001d);

        assertEquals(0d, mean.squaredDeviation(7), 0.000001d);
        assertEquals(1d, mean.squaredDeviation(6), 0.000001d);
    }

    @Test
    public void meanOfManyValues() {
        Mean mean = new Mean(Arrays.asList(3,4,5));

        assertEquals(3, mean.getQuantity());
        assertEquals(12, mean.getSummation());
        assertEquals(4d, mean.getValue(), 0.000001d);

        assertEquals(0d, mean.squaredDeviation(4), 0.000001d);
        assertEquals(4d, mean.squaredDeviation(6), 0.000001d);
    }
}
