package morse.cache;

import morse.models.SignalMeaning;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

@Component
public class MorseCache {
    private final Map<String, SignalMeaning> cache = new HashMap<>();

    public Mono<SignalMeaning> retrieve(String key) {
        return Mono.justOrEmpty(cache.get(key));
    }

    public void save(String key, SignalMeaning value) {
        cache.put(key, value);
    }
}
