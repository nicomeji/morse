package morse.utils.statistics;

import org.junit.Test;

import java.math.BigDecimal;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class RangeTest {
    @Test
    public void rangeIsClosedAtLimits() {
        Range<BigDecimal> range = new Range<>(BigDecimal.valueOf(1), BigDecimal.valueOf(5));

        assertFalse(range.contains(BigDecimal.valueOf(0)));
        assertTrue(range.contains(BigDecimal.valueOf(1)));
        assertTrue(range.contains(BigDecimal.valueOf(3)));
        assertTrue(range.contains(BigDecimal.valueOf(5)));
        assertFalse(range.contains(BigDecimal.valueOf(6)));
    }
}
