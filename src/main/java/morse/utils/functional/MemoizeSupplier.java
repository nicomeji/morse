package morse.utils.functional;

import lombok.AllArgsConstructor;

import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Supplier;

@AllArgsConstructor
public class MemoizeSupplier<T> implements Supplier<T> {
    private final AtomicReference<T> ref = new AtomicReference<>();
    private final Supplier<T> supplier;

    @Override
    public T get() {
        final T value = ref.get();
        if (value != null) {
            return value;
        } else {
            return loadValue();
        }
    }

    private synchronized T loadValue() {
        final T newValue = supplier.get();
        ref.set(newValue);
        return newValue;
    }
}
