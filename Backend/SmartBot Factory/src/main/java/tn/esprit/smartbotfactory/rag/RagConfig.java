package tn.esprit.smartbotfactory.rag;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
@EnableConfigurationProperties(RagProperties.class)
public class RagConfig {

    @Bean
    public WebClient qdrantWebClient(RagProperties props) {
        WebClient.Builder b = WebClient.builder()
                .baseUrl(props.getQdrant().baseUrl()) // ✅ accès à Qdrant
                .exchangeStrategies(ExchangeStrategies.builder()
                        .codecs(c -> c.defaultCodecs().maxInMemorySize(16 * 1024 * 1024))
                        .build());

        if (StringUtils.hasText(props.getQdrant().getApiKey())) { // ✅ via getQdrant()
            b.defaultHeader(HttpHeaders.AUTHORIZATION, "Bearer " + props.getQdrant().getApiKey());
        }
        return b.build();
    }
}
