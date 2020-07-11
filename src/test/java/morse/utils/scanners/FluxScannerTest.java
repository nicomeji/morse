package morse.utils.scanners;

import morse.utils.scanners.FluxScanner.Scanner;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.LinkedList;
import java.util.function.Consumer;

public class FluxScannerTest {
    @Test
    public void simple() {
        var msg = new StringBuilder();
        var fluxScanner = new FluxScanner<>(() -> (element, next) -> next.accept(msg.append(element).toString()));

        StepVerifier.create(fluxScanner.apply(Flux.just(0, 1, 2, 3)))
                .expectNext("0")
                .expectNext("01")
                .expectNext("012")
                .expectNext("0123")
                .expectNext("0123")
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }

    @Test
    public void carousel() {
        var values = new LinkedList<String>();
        var fluxScanner = new FluxScanner<String, String>(() -> (element, next) -> {
            values.add(element);
            if (values.size() == 3) {
                next.accept(String.join("", values));
                values.removeFirst();
            }
        });

        StepVerifier.create(fluxScanner.apply(Flux.just("0", "1", "2", "3", "4", "5")))
                .expectNext("012")
                .expectNext("123")
                .expectNext("234")
                .expectNext("345")
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }

    @Test
    public void handlingCompleteEvent() {
        var values = new LinkedList<String>();
        var fluxScanner = new FluxScanner<String, String>(() -> new Scanner<String, String>() {
            @Override
            public void accept(String element, Consumer<String> next) {
                values.add(element);
                next.accept(String.join("", values));
                if (values.size() == 3) {
                    values.removeFirst();
                }
            }

            @Override
            public void complete(Consumer<String> next) {
                while (!values.isEmpty()) {
                    next.accept(String.join("", values));
                    values.removeFirst();
                }
            }
        });

        StepVerifier.create(fluxScanner.apply(Flux.just("0", "1", "2", "3", "4", "5")))
                .expectNext("0")
                .expectNext("01")
                .expectNext("012")
                .expectNext("123")
                .expectNext("234")
                .expectNext("345")
                .expectNext("45")
                .expectNext("5")
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }
}
