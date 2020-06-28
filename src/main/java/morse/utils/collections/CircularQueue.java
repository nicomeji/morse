package morse.utils.collections;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import java.util.AbstractQueue;
import static java.util.Collections.*;
import java.util.Iterator;
import java.util.LinkedList;

@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class CircularQueue<T> extends AbstractQueue<T> {
    private final LinkedList<T> buffer = new LinkedList<>();

    @Getter
    private final int capacity;

    @Override
    public Iterator<T> iterator() {
        return unmodifiableList(buffer).iterator();
    }

    @Override
    public int size() {
        return buffer.size();
    }

    @Override
    public synchronized boolean offer(T element) {
        if (buffer.size() == capacity) {
            buffer.removeFirst();
        }
        return buffer.add(element);
    }

    @Override
    public T poll() {
        return buffer.poll();
    }

    @Override
    public T peek() {
        return buffer.peek();
    }
}
