package morse.config;

import morse.signal.SignalProcessor;
import morse.signal.scanners.StateValueScannerFactory;
import morse.utils.mappers.FluxScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignalProcessorConfig {
    @Bean
    public SignalProcessor signalProcessor(StateValueScannerFactory scannerFactory) {
        return new SignalProcessor(new FluxScanner<>(scannerFactory));
    }
}
