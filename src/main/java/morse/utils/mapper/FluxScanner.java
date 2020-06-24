package morse.utils.mapper;

import lombok.AllArgsConstructor;
import org.reactivestreams.Subscriber;
import org.reactivestreams.Subscription;
import reactor.core.publisher.Flux;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * This FluxScanner uses a Scanner which may keep track of previously processed values.
 * So, in order to avoid any kind of side effect it must be initialize before process any Flux.
 *
 * @param <T> From type
 * @param <U> To type
 */
@AllArgsConstructor
public class FluxScanner<T, U> implements Function<Flux<T>, Flux<U>> {
    private final Supplier<Scanner<T, U>> mapperSupplier;

    @Override
    public Flux<U> apply(Flux<T> flux) {
        final Scanner<T, U> mapper = mapperSupplier.get();

        return flux.materialize()
                .flatMap(signal -> Flux.create(sink -> signal.accept(new Subscriber<>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {
                        subscription.request(Long.MAX_VALUE);
                    }

                    @Override
                    public void onNext(T element) {
                        mapper.map(element, sink::next);
                        sink.complete();
                    }

                    @Override
                    public void onError(Throwable throwable) {
                        sink.error(throwable);
                    }

                    @Override
                    public void onComplete() {
                        mapper.complete(sink::next);
                        sink.complete();
                    }
                })));
    }

    /**
     * Scanner is used in stochastic process; it buffered data to define relations
     * between possible values. This means it's behaviour may not be deterministic
     * while collecting data, but once it has achieve a stable state it could
     * process inputs in a deterministic way.
     *
     * While Scanner is buffering data it may not push any next mapped value.
     * And as Flux size is undetermined during runtime, it may complete before StatefulMapper
     * achieves any stable state. So:
     * - "map" is used for each onNext value of the Flux; may not puh any mapped data.
     * - "complete" is used when Flux completes, so mapper can flush any remaining data.
     *
     * @param <T> From type
     * @param <U> To type
     */
    public interface Scanner<T, U> {
        void map(T element, Consumer<U> next);

        void complete(Consumer<U> next);
    }
}
