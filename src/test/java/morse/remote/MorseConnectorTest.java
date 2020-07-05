package morse.remote;

import com.github.tomakehurst.wiremock.WireMockServer;
import morse.config.MorseConnectorConfig;
import morse.models.SignalMeaning;
import morse.translator.SignalTranslator;
import org.junit.BeforeClass;
import org.junit.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class MorseConnectorTest {
    private static final String MORSE = "/morse";
    private static final String CODE = "code";
    private static final String TIME_OUT = "time_out";
    private static final String ERROR = "error";
    private static final String INVALID_REQ = "invalid_request";
    private static final String INVALID_RES = "invalid_response";
    private static final String O_MORSE = "---";
    private static final String S_MORSE = "...";

    private static WireMockServer mockServer;
    private static MorseConnector connector;

    @BeforeClass
    public static void init() {
        mockServer = new WireMockServer(0);
        mockServer.start();
        connector = connector(mockServer.port());

        mockServer.stubFor(get(urlPathEqualTo(MORSE)).withQueryParam(CODE, equalTo(O_MORSE))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withFixedDelay((int) Duration.ofSeconds(1).toMillis())
                                .withHeader("Content-Type", "text/plain")
                                .withBody("o")));

        mockServer.stubFor(get(urlPathEqualTo(MORSE)).withQueryParam(CODE, equalTo(S_MORSE))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "text/plain")
                                .withBody("s")));

        mockServer.stubFor(get(urlPathEqualTo(MORSE)).withQueryParam(CODE, equalTo(SignalTranslator.EOF))
                .willReturn(
                        aResponse()
                                .withStatus(404)
                                .withHeader("Content-Type", "text/plain")
                                .withBody("Not found")));

        mockServer.stubFor(get(urlPathEqualTo(MORSE)).withQueryParam(CODE, equalTo(TIME_OUT))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withFixedDelay((int) Duration.ofSeconds(3).toMillis())));

        mockServer.stubFor(get(urlPathEqualTo(MORSE)).withQueryParam(CODE, equalTo(ERROR))
                .willReturn(
                        aResponse()
                                .withStatus(500)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody("{\"error\": \"RemoteError\"}")));

        mockServer.stubFor(get(urlPathEqualTo(MORSE)).withQueryParam(CODE, equalTo(INVALID_REQ))
                .willReturn(
                        aResponse()
                                .withStatus(400)
                                .withHeader("Content-Type", "application/json; charset=utf-8")
                                .withBody("{\"error\": \"RemoteError\"}")));

        mockServer.stubFor(get(urlPathEqualTo(MORSE)).withQueryParam(CODE, equalTo(INVALID_RES))
                .willReturn(
                        aResponse()
                                .withStatus(200)
                                .withHeader("Content-Type", "text/plain")));
    }

    @Test
    public void testConnector() {
        Flux<Character> message = Flux.just(S_MORSE, O_MORSE, S_MORSE)
                .concatMap(connector::requestTranslation)
                .map(SignalMeaning::getCharacter);

        StepVerifier.create(message)
                .expectNext('s')
                .expectNext('o')
                .expectNext('s')
                .expectComplete()
                .verify(Duration.ofSeconds(3));
    }

    private static MorseConnector connector(int port) {
        MorseConnectorConfig connectorConfig = new MorseConnectorConfig();
        connectorConfig.setBaseUrl("http://localhost:" + port + MORSE);
        return connectorConfig.morseConnector();
    }
}
