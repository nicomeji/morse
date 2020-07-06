package morse.config;

import morse.signal.SignalDecoder;
import morse.signal.SignalSegmentation;
import morse.signal.scanners.StateValueScannerFactory;
import morse.utils.mappers.FluxScanner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SignalDecoderConfig {
    @Bean
    public SignalDecoder signalDecoder(SignalSegmentation segmentation, StateValueScannerFactory scannerFactory) {
        return new SignalDecoder(segmentation, new FluxScanner<>(scannerFactory));
    }
}
