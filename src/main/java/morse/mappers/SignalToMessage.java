package morse.mappers;

import morse.models.SignalMeaning;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class SignalToMessage {
    public Mono<String> map(Flux<SignalMeaning> signal) {
        return signal.collectList()
                .map(chars -> {
                    StringBuilder sb = new StringBuilder();
                    chars.forEach(sb::append);
                    return sb.toString();
                });
    }
}
