package morse.utils.statistics;

import static org.junit.Assert.*;
import org.junit.Test;

public class MinimizerTest {
    @Test
    public void searchMin_1() {
        Minimizer quadraticMin = new Minimizer(
                new Range<>(-5, 5),
                x -> x == 1 ? 0d : 1d);

        assertEquals(1, quadraticMin.relativeMin(2));
        assertEquals(1, quadraticMin.relativeMin(5));
    }

    @Test
    public void searchMin_2() {
        Minimizer quadraticMin = new Minimizer(
                new Range<>(2, 14),
                x -> x % 5 == 0 ? 0d : 1d);

        assertEquals(5, quadraticMin.relativeMin(2));
        assertEquals(10, quadraticMin.relativeMin(12));
    }

    @Test
    public void searchMinOfQuadratic_1() {
        Minimizer quadraticMin = new Minimizer(
                new Range<>(-5, 5),
                x -> Math.pow((double) x, 2));

        assertEquals(0, quadraticMin.relativeMin(-2));
        assertEquals(0, quadraticMin.relativeMin(0));
        assertEquals(0, quadraticMin.relativeMin(2));
    }

    @Test
    public void searchMinOfQuadratic_2() {
        Minimizer quadraticMin = new Minimizer(
                new Range<>(1, 5),
                x -> Math.pow((double) x, 2));

        assertEquals(1, quadraticMin.relativeMin(2));
        assertEquals(1, quadraticMin.relativeMin(5));
    }
}
