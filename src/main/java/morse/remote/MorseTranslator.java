package morse.remote;

import morse.config.CacheConfig;
import morse.models.SignalMeaning;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class MorseTranslator {
    @Cacheable(CacheConfig.CODES_MEANING)
    public Mono<SignalMeaning> translate(String morse) {
        return null;
    }
}
