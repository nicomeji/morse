package morse.utils.scanners;

import morse.utils.scanners.FluxScanner.Scanner;
import org.junit.Ignore;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Scheduler;
import reactor.core.scheduler.Schedulers;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.LinkedList;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Stream;

public class FluxScannerTest {
    @Test
    public void simple() {
        var msg = new StringBuilder();
        var fluxScanner = new FluxScanner<>((element, next) -> next.accept(msg.append(element).toString()));

        StepVerifier.create(fluxScanner.apply(Flux.just(0, 1, 2, 3)))
                .expectNext("0")
                .expectNext("01")
                .expectNext("012")
                .expectNext("0123")
                .expectComplete()
                .verify(Duration.ofMillis(100));
    }

    /**
     * "values" is a circular queue of size 3, once it's full it's content is pushed.
     */
    @Test
    public void carousel() {
        var values = new LinkedList<String>();
        var fluxScanner = new FluxScanner<String, String>((element, next) -> {
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

    /**
     * "values" is a circular queue of size 3, which content is pushed with every next element.
     * Once flux is completed, it's keep pushing it's content until it's empty.
     */
    @Test
    public void handlingCompleteEvent() {
        var values = new LinkedList<String>();
        var fluxScanner = new FluxScanner<String, String>(new Scanner<>() {
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

    /**
     * "values" is a circular queue of size 3.
     * If "values" contains "3", then pushing is delayed.
     */
    @Test
    @Ignore
    public void handlingAsyncEvents() {
        var values = new LinkedList<String>();
        var fluxScanner = new FluxScanner<String, String>((element, next) -> {
            values.add(element);
            if (values.size() == 3) {
                var flux = Flux.fromIterable(new LinkedList<>(values));
                flux.collectList()
                        .map(list -> {
                            var msg = String.join("", list);
                            if(msg.contains("3")) {
                                delay();
                            }
                            printNext(msg);
                            return msg;
                        })
                        .blockOptional()
                        .ifPresent(next);
                values.removeFirst();
            }
        });

        final Scheduler scheduler = Schedulers.fromExecutor(Executors.newFixedThreadPool(3));
        final Flux<String> messages = fluxScanner
                .apply(Flux.fromStream(Stream.iterate(0, i -> i + 1).limit(36).map(Objects::toString))
                        .doOnNext(this::printNext))
                .subscribeOn(scheduler);

        StepVerifier.create(messages)
                .expectNext("012")
                .expectNext("123")
                .expectNext("234")
                .expectNext("345")
                .expectNext("456")
                .expectNext("567")
                .expectNext("678")
                .expectNext("789")
                .expectNext("8910")
                .expectNext("91011")
                .expectNext("101112")
                .expectNext("111213")
                .expectNext("121314")
                .expectNext("131415")
                .expectNext("141516")
                .expectNext("151617")
                .expectNext("161718")
                .expectNext("171819")
                .expectNext("181920")
                .expectNext("192021")
                .expectNext("202122")
                .expectNext("212223")
                .expectNext("222324")
                .expectNext("232425")
                .expectNext("242526")
                .expectNext("252627")
                .expectNext("262728")
                .expectNext("272829")
                .expectNext("282930")
                .expectNext("293031")
                .expectNext("303132")
                .expectNext("313233")
                .expectNext("323334")
                .expectNext("333435")
                .expectComplete()
                .verify(Duration.ofMillis(10000));
    }

    private void delay() {
        try {
            TimeUnit.MILLISECONDS.sleep(300);
        } catch (InterruptedException e) {
            throw new RuntimeException();
        }
    }

    private void printNext(String msg) {
        System.out.println("thread: " + Thread.currentThread().getName() + ", msg: " + msg);
    }
}
