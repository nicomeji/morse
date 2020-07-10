package morse.utils.scanners;

import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class FluxScannerTest {
    private final Supplier<FluxScanner.Scanner<Integer, String>> mapperSupplier = () ->
            new FluxScanner.Scanner<>() {
                private StringBuilder msg = new StringBuilder();

                @Override
                public void map(Integer element, Consumer<String> next) {
                    next.accept(msg.append(element).toString());
                }

                @Override
                public void complete(Consumer<String> next) {
                    next.accept(msg.toString());
                }
            };

    private final FluxScanner<Integer, String> fluxMapper = new FluxScanner<>(mapperSupplier);

    @Test
    public void testMapping() {
        StepVerifier.create(fluxMapper.apply(Flux.just(0, 1, 2, 3)))
                .expectNext("0")
                .expectNext("01")
                .expectNext("012")
                .expectNext("0123")
                .expectNext("0123")
                .expectComplete()
                .verify(Duration.ofMillis(10));
    }
}
