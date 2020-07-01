package morse.controllers;

import lombok.AllArgsConstructor;
import morse.mappers.RequestToSignal;
import morse.mappers.SignalToMessage;
import morse.service.MorseService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.util.List;

@Validated
@AllArgsConstructor
@RestController
@RequestMapping("/morse")
public class MorseController {
    private final MorseService service;
    private final RequestToSignal requestToSignal;
    private final SignalToMessage signalToMessage;

    @PostMapping("/decode")
    public Mono<String> message(@Valid @RequestBody List<@NotNull Integer> morseMessage) {
        return signalToMessage.map(service.decode(requestToSignal.map(morseMessage)));
    }
}
