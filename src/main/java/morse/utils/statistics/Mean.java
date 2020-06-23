package morse.utils.statistics;

import lombok.EqualsAndHashCode;
import morse.utils.functional.MemoizeSupplier;

import java.math.BigDecimal;
import java.util.function.Supplier;

import static java.math.RoundingMode.CEILING;

@EqualsAndHashCode(callSuper = true)
public class Mean extends Number implements Supplier<BigDecimal>, Comparable<Number> {
    private static final long serialVersionUID = 1L;
    private static final int SCALE = 5;

    private final BigDecimal summation;
    private final int qty;

    @EqualsAndHashCode.Exclude
    private final transient Supplier<BigDecimal> value;

    public Mean(Number n) {
        this(new BigDecimal(n.toString()), 1);
    }

    private Mean(BigDecimal summation, int qty) {
        this.summation = summation;
        this.qty = qty;
        this.value = new MemoizeSupplier<>(() -> summation.divide(BigDecimal.valueOf(qty), SCALE, CEILING));
    }

    public Mean add(Number n) {
        return new Mean(summation.add(new BigDecimal(n.toString())), qty + 1);
    }

    public Mean combine(Mean other) {
        return new Mean(summation.add(other.summation), qty + other.qty);
    }

    public BigDecimal squaredDeviation(Number n) {
        BigDecimal number = new BigDecimal(n.toString());
        return number.subtract(get()).pow(2);
    }

    @Override
    public BigDecimal get() {
        return value.get();
    }

    @Override
    public int intValue() {
        return value.get().intValue();
    }

    @Override
    public long longValue() {
        return value.get().longValue();
    }

    @Override
    public float floatValue() {
        return value.get().floatValue();
    }

    @Override
    public double doubleValue() {
        return value.get().doubleValue();
    }

    @Override
    public String toString() {
        return value.get().toString();
    }

    @Override
    public int compareTo(Number o) {
        return value.get().compareTo(new BigDecimal(o.toString()));
    }
}
