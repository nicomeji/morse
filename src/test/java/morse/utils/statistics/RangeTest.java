package morse.utils.statistics;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.Arrays;

import static org.junit.Assert.*;

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

    @Test
    public void rangeCanBeCreateToContainValues() {
        Range<Integer> range = Range.containing(Arrays.asList(1, 5, 3, -4, 0, 9));

        assertEquals(-4L, (long)range.getFrom());
        assertEquals(9L, (long)range.getTo());
        assertFalse(range.contains(-5));
        assertTrue(range.contains(-4));
        assertTrue(range.contains(1));
        assertTrue(range.contains(9));
        assertFalse(range.contains(10));
    }
}
