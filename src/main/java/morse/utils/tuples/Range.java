package morse.utils.tuples;

import lombok.Data;

@Data
public class Range<T extends Comparable<T>> {
    private final T from;
    private final T to;

    public Range(T t1, T t2) {
        if (t1.compareTo(t2) < 0) {
            this.from = t1;
            this.to = t2;
        } else {
            this.from = t2;
            this.to = t1;
        }
    }

    public boolean contains(T t) {
        return from.compareTo(t) <= 0 && t.compareTo(to) <= 0;
    }
}
