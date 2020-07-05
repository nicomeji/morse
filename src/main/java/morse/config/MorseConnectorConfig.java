package morse.config;

import lombok.Data;
import morse.remote.MorseConnector;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Data
@Configuration
@ConfigurationProperties(prefix = "connector.translator")
public class MorseConnectorConfig {
    private String baseUrl;

    @Bean
    public MorseConnector morseConnector() {
        return new MorseConnector(WebClient.builder()
                .baseUrl(baseUrl)
                .build());
    }
}
