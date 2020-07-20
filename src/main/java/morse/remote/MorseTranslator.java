package morse.remote;

import lombok.AllArgsConstructor;
import morse.cache.MorseCache;
import morse.models.SignalMeaning;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class MorseTranslator {
    private final MorseCache morseCache;
    private final MorseConnector morseConnector;

    public Mono<SignalMeaning> translate(final String morseCode) {
        final Mono<SignalMeaning> retrieve = morseCache.retrieve(morseCode);
        return retrieve.switchIfEmpty(
                morseConnector.requestTranslation(morseCode)
                        .doOnNext(signalMeaning -> morseCache.save(morseCode, signalMeaning)));
    }

    public Flux<SignalMeaning> translate(final List<String> codes) {
        return request(fromCache(codes), codes).flatMapMany(Flux::fromIterable);
    }

    private Mono<List<SignalMeaning>> fromCache(final List<String> codes) {
        return Flux.concat(codes.stream()
                .map(morseCache::retrieve)
                .collect(Collectors.toList()))
                .log()
                .collectList();
    }

    private Mono<List<SignalMeaning>> request(final Mono<List<SignalMeaning>> fromCache, final List<String> codes) {
        final var fromCacheCached = fromCache.cache();
        return Flux.fromIterable(codes)
                .filterWhen(code -> fromCacheCached.map(cached -> !isAlreadyCached(cached, code)))
                .collectList()
                .flatMap(morseConnector::requestTranslation)
                .zipWith(fromCacheCached, this::concat)
                .map(items -> items.stream()
                        .sorted(Comparator.comparingInt(s -> codes.indexOf(s.getMorse())))
                        .collect(Collectors.toList()));
    }

    private boolean isAlreadyCached(List<SignalMeaning> fromCache, String code) {
        return fromCache.stream().anyMatch(s -> s.getMorse().equals(code));
    }

    private List<SignalMeaning> concat(List<SignalMeaning> l1, List<SignalMeaning> l2) {
        var concatenation = new ArrayList<>(l1);
        concatenation.addAll(l2);
        return concatenation;
    }
}
