package morse.utils.collections;

import org.junit.Test;

import java.util.Queue;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CircularQueueTest {
    @Test
    public void circularQueueHasFixedCapacity() {
        CircularQueue<Integer> queue = new CircularQueue<>(4);

        assertEquals(0, queue.size());
        assertEquals(4, queue.getCapacity());
        assertNull(queue.poll());
    }

    @Test
    public void circularQueueIsFIFO() {
        Queue<Integer> queue = new CircularQueue<>(3);
        queue.add(1);
        queue.add(2);
        queue.add(3);

        assertEquals(3, queue.size());
        assertEquals(1, (int) queue.poll());
        assertEquals(2, (int) queue.poll());
        assertEquals(3, (int) queue.poll());
        assertEquals(0, queue.size());
    }

    @Test
    public void circularQueueDropsLatestElements() {
        CircularQueue<Integer> queue = new CircularQueue<>(3);
        queue.add(1);
        queue.add(2);
        queue.add(3);
        queue.add(4);

        assertEquals(3, queue.size());
        assertEquals(2, (int) queue.poll());
        assertEquals(3, (int) queue.poll());
        assertEquals(4, (int) queue.poll());
        assertEquals(0, queue.size());
    }
}
