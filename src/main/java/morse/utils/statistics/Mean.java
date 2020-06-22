package morse.utils.statistics;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class Mean {
    private static final int SCALE = 5;

    private BigDecimal summation;
    private int qty;

    public Mean(Number n) {
        summation = new BigDecimal(n.toString());
        qty = 1;
    }

    public void add(Number n) {
        summation = summation.add(new BigDecimal(n.toString()));
        qty ++;
    }

    public BigDecimal get() {
        return summation.divide(BigDecimal.valueOf(qty), SCALE, RoundingMode.CEILING);
    }

    public BigDecimal squaredDeviation(Number n) {
        BigDecimal number = new BigDecimal(n.toString());
        return number.subtract(get()).pow(2);
    }
}
